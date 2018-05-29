package pers.gene.ticketmanagement.web.controller;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pers.gene.ticketmanagement.domain.Customer;
import pers.gene.ticketmanagement.service.CustomerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//这个注解导致返回字符串
//@RestController
//@ResponseBody
@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);


    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        return "login";
    }
    /**
     * 注册
     * @param request
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String regist(HttpServletRequest request) {
//        CustomerService service = new CustomerService();
        if (request.getParameter("passwordsignup").equals(request.getParameter("passwordsignup_confirm"))) {
            Customer customer = new Customer();
            customer.setId(UUID.randomUUID().toString());
            customer.setUserName(request.getParameter("usernamesignup"));
//            customer.setPassword(request.getParameter("passwordsignup"));
            //使用BCryptPasswordEncoder管理密码
            customer.setPassword(DigestUtils.md5DigestAsHex((request.getParameter("passwordsignup")).getBytes()));
            customer.setEmail(request.getParameter("emailsignup"));
            customer.setCellphone(request.getParameter("cellphonesignup"));
            customerService.regist(customer);
//            confirm("注册成功")；
            return "success";
        } else {
            return "passwordfail";
        }
    }


//    @RequestMapping("/login")
//    public String login(HttpServletRequest request) {
//        String userName = request.getParameter("username");
//        String password = request.getParameter("password");
//        if (checkEmailFormat(userName)) {
//            request.getSession().setAttribute("customer", customerService.getCustomerByEmail(userName));
//        } else {
//            request.getSession().setAttribute("customer", customerService.getCustomerByUserName(userName));
//        }
//        return customerService.login(userName, password);
//    }

    //验证邮箱格式
    boolean checkEmailFormat(String email) {
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @RequestMapping("/index")
    public String go() {
        return "index";
    }


    @RequestMapping("/success")
    public String success() {
        return "success";
    }

    @RequestMapping("/fail")
    public String fail(){
        return "fail";
    }

    @RequestMapping("myProfile")
    public String myProfile(){
        return "myProfile";
    }
    @RequestMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               HttpServletRequest request) throws IOException{
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        /*System.out.println("fileName-->" + fileName);
        System.out.println("getContentType-->" + contentType);*/
        String filePath = request.getSession().getServletContext().getRealPath("imgupload/");
        try {
            uploadFile(file.getBytes(), filePath, fileName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "success";
    }
    void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }

}
