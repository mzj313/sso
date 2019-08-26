package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.dao.UserEmailTelDao;
import com.chinasofti.oauth2.asserver.entity.UserEmailTel;
import com.chinasofti.oauth2.asserver.utils.ApplicationUtil;
import com.chinasofti.oauth2.asserver.utils.EmailUtil;
import com.chinasofti.oauth2.asserver.utils.JedisUtil;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;

/**
 * Created by makai on 2016/1/21 0021.
 */
@Transactional
@Service
public class UserEmailTelServiceImpl implements UserEmailTelService{
    @Autowired
    private UserEmailTelDao userEmailTelDao;

    @Override
    public UserEmailTel fingOne(String userId) {
        UserEmailTel userEmailTel = userEmailTelDao.findOne(userId);
        return userEmailTel;
    }

    /**
     * 验证 手机验证码 成功后保存
     * @param userId
     * @param email
     * @param tel
     * @param code
     * @return
     */
    @Override
    public String update(String userId, String email, String tel,String code,String emailCode) {
        JsonObject jsonObject = new JsonObject();
        //验证手机验证码
        if(!org.springframework.util.StringUtils.isEmpty(tel)){ //验证是否输入手机号码
            if(org.springframework.util.StringUtils.isEmpty(code)){//没有输入验证码
                jsonObject.addProperty("state", 250);
                jsonObject.addProperty("msg", "请输入验证码");
                return jsonObject.toString();
            }
            String openJedis = ApplicationUtil.getValue("open_redis") ;
            if(StringUtils.isNotBlank(openJedis) && "true".equals(openJedis.trim())){
                String resCode = JedisUtil.getString(userId+"-phone");
                if(resCode == null ){
                    jsonObject.addProperty("state", 250);
                    jsonObject.addProperty("msg", "手机验证码已过期,请重新发送");
                    return jsonObject.toString();
                }
                //tel--code  组成验证码
                if((tel+"--"+code).equals(resCode)){
                    //验证码正确,做后续操作
                }else{
                    jsonObject.addProperty("state", 250);
                    jsonObject.addProperty("msg", "手机验证码错误");
                    return jsonObject.toString();
                }
            }else{
                jsonObject.addProperty("state", 250);
                jsonObject.addProperty("msg", "系统异常,请稍后重试");
                return jsonObject.toString();
            }
        }

        //验证邮箱验证码
        if(!org.springframework.util.StringUtils.isEmpty(email)){ //验证email是否为空
            if(org.springframework.util.StringUtils.isEmpty(emailCode)){//没有输入验证码
                jsonObject.addProperty("state", 250);
                jsonObject.addProperty("msg", "请输入验证码");
                return jsonObject.toString();
            }
            String openJedis = ApplicationUtil.getValue("open_redis") ;
            if(StringUtils.isNotBlank(openJedis) && "true".equals(openJedis.trim())){
                String resCode = JedisUtil.getString(userId+"-email");
                if(resCode == null ){
                    jsonObject.addProperty("state", 250);
                    jsonObject.addProperty("msg", "邮箱验证码已过期,请重新发送");
                    return jsonObject.toString();
                }
                //tel--code  组成验证码
                if((email+"--"+emailCode).equals(resCode)){
                    //验证码正确,做后续操作
                }else{
                    jsonObject.addProperty("state", 250);
                    jsonObject.addProperty("msg", "邮箱验证码错误");
                    return jsonObject.toString();
                }
            }else{
                jsonObject.addProperty("state", 250);
                jsonObject.addProperty("msg", "系统异常,请稍后重试");
                return jsonObject.toString();
            }
        }

        //修改信息
        userEmailTelDao.update(userId, email, tel);
        return null;
    }

    /**
     * 获取用户信息 包含登录名称 以及 真实姓名
     *
     * @param userId
     * @return
     */
    @Override
    public UserEmailTel findAccout(String userId) {
        UserEmailTel userEmailTel = userEmailTelDao.fingAccount(userId);
        return userEmailTel;
    }

    /**
     * 发送手机验证码
     *
     * @param userId
     * @param tel
     * @return
     */
    @Override
    public String sendCode(String userId, String tel) {
        JsonObject jsonObject = new JsonObject();
        //生成随机验证码(5位数字)
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 5 ; i++){
            sb.append((int)(Math.random()*10));
        }
        String code = sb.toString();

        //todo 发送验证码到手机

        //将验证码放入redis  时效2分钟
        String openJedis = ApplicationUtil.getValue("open_redis") ;
        if(StringUtils.isNotBlank(openJedis) && "true".equals(openJedis.trim())){
            //userId-phone key   tel--code 组成value值
            JedisUtil.saveString(userId+"-phone", tel+"--"+code, 120);
            jsonObject.addProperty("state", 200);
            jsonObject.addProperty("msg", "发送验证码成功");
            return jsonObject.toString();
        }else{
            jsonObject.addProperty("state", 250);
            jsonObject.addProperty("msg", "系统异常,请重新发送");
            return jsonObject.toString();
        }

    }

    @Override
    public String sendEmailCode(String userId,String email){
        JsonObject jsonObject = new JsonObject();
        //生成随机验证码(5位数字)
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 5 ; i++){
            sb.append((int)(Math.random()*10));
        }
        String code = sb.toString();

        try {
            EmailUtil.sendEmailBySMTP(email,"验证码："+code,"邮箱验证");
        } catch (MessagingException e) {
            e.printStackTrace();
            jsonObject.addProperty("state", 250);
            jsonObject.addProperty("msg", e.getMessage());
            return jsonObject.toString();
        }
        //将验证码放入redis  时效2分钟
        String openJedis = ApplicationUtil.getValue("open_redis") ;
        if(StringUtils.isNotBlank(openJedis) && "true".equals(openJedis.trim())){
            //tel--code 组成value值
            JedisUtil.saveString(userId+"-email", email+"--"+code, 120);
            jsonObject.addProperty("state", 200);
            jsonObject.addProperty("msg", "发送验证码成功");
            return jsonObject.toString();
        }else{
            jsonObject.addProperty("state", 250);
            jsonObject.addProperty("msg", "系统异常,请重新发送");
            return jsonObject.toString();
        }
    }
}
