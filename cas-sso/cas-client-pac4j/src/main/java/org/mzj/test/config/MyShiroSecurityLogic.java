package org.mzj.test.config;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isEmpty;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;

import java.util.Arrays;
import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import io.buji.pac4j.profile.ShiroProfileManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MyShiroSecurityLogic<R, C extends WebContext> extends DefaultSecurityLogic<R, C> {

    public MyShiroSecurityLogic() {
        super();
        this.setProfileManagerFactory(ShiroProfileManager::new);
    }

    @Override
    public R perform(final C context, final Config config, final SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter,
                     final HttpActionAdapter<R, C> httpActionAdapter,
                     final String clients, final String authorizers, final String matchers, final Boolean inputMultiProfile,
                     final Object... parameters) {

        logger.info("=== SECURITY ===");

        HttpAction action;
        try {

            // default value
            final boolean multiProfile;
            if (inputMultiProfile == null) {
                multiProfile = false;
            } else {
                multiProfile = inputMultiProfile;
            }

            // checks
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotNull("clientFinder", this.getClientFinder());
            assertNotNull("authorizationChecker", this.getAuthorizationChecker());
            assertNotNull("matchingChecker", this.getMatchingChecker());
            assertNotNull("profileStorageDecision", this.getProfileStorageDecision());
            final Clients configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            logger.info("url: {}", context.getFullRequestURL());
            logger.debug("matchers: {}", matchers);
            if (this.getMatchingChecker().matches(context, matchers, config.getMatchers())) {

                logger.debug("clients: {}", clients);
                final List<Client> currentClients = this.getClientFinder().find(configClients, context, clients);
                logger.debug("currentClients: {}", currentClients);

                final boolean loadProfilesFromSession = this.getProfileStorageDecision().mustLoadProfilesFromSession(context, currentClients);
                logger.debug("loadProfilesFromSession: {}", loadProfilesFromSession);
                final ProfileManager manager = getProfileManager(context, config);
                List<CommonProfile> profiles = manager.getAll(loadProfilesFromSession);
                logger.info("profiles: {}", profiles);

                // no profile and some current clients
                if (isEmpty(profiles) && isNotEmpty(currentClients)) {
                    boolean updated = false;
                    // loop on all clients searching direct ones to perform authentication
                    for (final Client currentClient : currentClients) {
                        if (currentClient instanceof DirectClient) {
                            logger.debug("Performing authentication for direct client: {}", currentClient);

                            final Credentials credentials = currentClient.getCredentials(context);
                            logger.info("credentials: {}", credentials);
                            final CommonProfile profile = currentClient.getUserProfile(credentials, context);
                            logger.info("profile: {}", profile);
                            if (profile != null) {
								final boolean saveProfileInSession = this.getProfileStorageDecision().mustSaveProfileInSession(context,
                                    currentClients, (DirectClient) currentClient, profile);
                                logger.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                                manager.save(saveProfileInSession, profile, multiProfile);
                                updated = true;
                                if (!multiProfile) {
                                    break;
                                }
                            }
                        }
                    }
                    if (updated) {
                        profiles = manager.getAll(loadProfilesFromSession);
                        logger.debug("new profiles: {}", profiles);
                    }
                }

                // we have profile(s) -> check authorizations
                if (isNotEmpty(profiles)) {
                    logger.debug("authorizers: {}", authorizers);
                    if (this.getAuthorizationChecker().isAuthorized(context, profiles, authorizers, config.getAuthorizers())) {
                        logger.info("authenticated and authorized -> grant access");
                        return securityGrantedAccessAdapter.adapt(context, profiles, parameters);
                    } else {
                        logger.info("forbidden");
                        action = forbidden(context, currentClients, profiles, authorizers);
                    }
                } else {
                    if (startAuthentication(context, currentClients)) {
                        logger.info("Starting authentication");
                        saveRequestedUrl(context, currentClients);
                        action = redirectToIdentityProvider(context, currentClients);
                    } else {
                        logger.info("unauthorized");
                        action = unauthorized(context, currentClients);
                    }
                }

            } else {

                logger.debug("no matching for this request -> grant access");
                return securityGrantedAccessAdapter.adapt(context, Arrays.asList(), parameters);
            }

        } catch (final Exception e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }
}