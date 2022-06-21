package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.HanZiUtil;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("sendMsg")
    public R<String> sendPhone(@RequestBody User user, HttpSession session) {

        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            System.out.println("验证码code:" + code);
            SMSUtils.sendPhone(phone, code);
//            需要将生成的验证码保存到Session
//            session.setAttribute("phone", code);

            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("发送短信成功");
        }

        return R.error("发送短信失败");
    }


    /**
     * 用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
//            获取用户信息
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute("phone");
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession != null && codeInSession.equals(codeInSession)) {
//            登录成功,判断是否为新用户
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
//                新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName(HanZiUtil.getRandomHanZi(3));
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

//            登录成功
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败");

    }
}