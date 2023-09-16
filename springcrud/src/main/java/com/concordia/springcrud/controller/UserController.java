package com.concordia.springcrud.controller;

import com.concordia.springcrud.entity.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.io.File;
@Controller
//@RequestMapping("/")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String login(Model theModel , HttpSession session) {

        String username = (String) session.getAttribute("user");
        if(username == null || username.isEmpty()){
            theModel.addAttribute("isLogin", false);
            return "other/login";
        }
        theModel.addAttribute("isLogin", true);
        return "redirect:/employees/list";
    }

    @PostMapping("/login")
    public String signIn(@RequestParam("username") String username
                , @RequestParam("password") String password   , HttpSession session )  {

        //sql injection input : john' or 1='1
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password ='" + password +"'";

        List<Map<String,Object>> res = jdbcTemplate.queryForList(sql);


        if (res.isEmpty()) {
            return "redirect:/";
        }else{
            session.setAttribute("user", username);

            return "redirect:/employees/list";
        }

    }

    @GetMapping("/logout")
    public String logout( HttpSession session )  {

            session.setAttribute("user", "");

            return "other/login";
    }

    @PostMapping("/upload1")
    @ResponseBody
    public String upload1( @RequestParam("file") MultipartFile file ,Model model)  {

        String filename = file.getOriginalFilename();
        System.out.println(filename);
        assert filename != null;

            String path="src/main/resources/static/";
            File fileDir = new File(path);
            File outfile = new File(fileDir.getAbsolutePath()+File.separator + filename);
            try {
                file.transferTo(outfile);
                return "success";
            }catch (IOException e){
                e.printStackTrace();
            }
        return "redirect:/employees/showFormForUpdate?employeeId=1";
    }

    @PostMapping("/upload2")
    @ResponseBody
    public String upload2( @RequestParam("file") MultipartFile file,Model model )  {
        boolean flag=true;
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        assert filename != null;
        String suffix=filename.substring(filename.lastIndexOf("."));
        String[] blacklist={".jsp",".php",".exe",".dll","vxd","html"};//后缀名黑名单
        for (String s : blacklist) {
            if (suffix.equals(s)){
                flag=false;
                break;

            }
        }
        if (flag){
            String path="src/main/resources/static/";
            File fileDir = new File(path);
            File outfile = new File(fileDir.getAbsolutePath()+File.separator + filename);
            try {
                file.transferTo(outfile);
                return "success";
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        else {
            model.addAttribute("msg","非法文件类型");
        }

        return "employees/showFormForUpdate";
    }
}
