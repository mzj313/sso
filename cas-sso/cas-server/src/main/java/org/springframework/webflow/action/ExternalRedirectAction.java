package org.springframework.webflow.action;

import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action that sends an external redirect when executed.
 * 
 * @author Keith Donald
 */
public class ExternalRedirectAction extends AbstractAction {

	private Expression resourceUri;

	/**
	 * Creates a new external redirect action
	 * @param resourceUri an expression for the resource Uri to redirect to
	 */
	public ExternalRedirectAction(Expression resourceUri) {
		Assert.notNull(resourceUri, "The URI of the resource to redirect to is required");
		this.resourceUri = resourceUri;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		String resourceUri = (String) this.resourceUri.getValue(context);
		logger.info("resourceUri= " + resourceUri);
		context.getExternalContext().requestExternalRedirect(resourceUri);
		return success();
	}

}
