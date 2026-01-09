package com.example.demo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 스포츠 센터
// 역할(ROLE - 인가) : user, manager, admin

@Log4j2
@Controller //- 각 메서드가 view를 리턴한다.
//@RestController -> 위 컨트롤러를 사용해야한다.
public class IndexController {
    @GetMapping({"","/"})
    public String index(){
        log.info("index");
        //->/WEB-INF/views/{{index}}.jsp (스프링부트가 내부적으로 실행)
        // 어노테이션이 RestController에서 Controller로 변경됨
        //@RestController = @Controller + @ResponseBody -> 문자열 포맷
        //@Controller => 문자열이 출력으로 나갈 화면 이름이다.
        return "index"; //->ViewResolver
    }//end of home
    //http://localhost:8000/user
    @GetMapping({"/user"})
    public String user(){
        log.info("home");
        return "home";
    }//end of home
    //http://localhost:8000/manager
    @GetMapping({"/manager"})
    public String manager(){
        log.info("manager");
        return "manager";
    }//end of home
    //http://localhost:8000/admin
    @GetMapping({"/admin"})
    public String admin(){
        log.info("admin");
        return "admin";
    }//end of home
    
    //로그인화면 요청하기
    //http://localhost:8000/loginForm
    @GetMapping("/loginForm")
    public String loginForm(){
        log.info("loginForm");
        return "auth/loginForm";
    }//end of loginForm
    
    //회원가입화면 호출하기
    //http://localhost:8000/joinForm
    @GetMapping("/joinForm")
    public String joinForm(){
        log.info("joinForm");
        //auth/joinForm -> 응답페이지의 화면 이름이다.
        //yaml -> /WEB-INF/views/ 접두어
        //접미어   ->.jsp
        return "auth/joinForm";
    }
    //에러 페이지 호출하기
    //http://localhost:8000/login-error
    @GetMapping("/login-error")
    public String loginError(){
        log.info("login-Error");
        return "loginError";
    }
}
