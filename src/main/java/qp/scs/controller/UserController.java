package qp.scs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import qp.scs.controller.BaseController;

import qp.scs.constant.Messages;
import qp.scs.dto.*;
import qp.scs.model.*;
import qp.scs.security.TokenAuthenticationService;


import qp.scs.constant.Codes;
//import com.sg.moe.pz.dto.AuthRequestDto;
//import com.sg.moe.pz.dto.AuthResponseDto;
//import com.sg.moe.pz.entity.AaSubjectBean;
//import com.sg.moe.pz.entity.AaSubjectLoginBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import qp.scs.dto.request.LoginRequestDTO;
import qp.scs.dto.request.RequestDTO;
import qp.scs.dto.response.LoginResponseDTO;
import qp.scs.dto.response.ProfileDetailResponseDTO;
import qp.scs.model.User;
import qp.scs.service.UserService;
import qp.scs.util.*;

@RestController
@RequestMapping(path = "api/protected/users/")
public class UserController  extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@RequestMapping(path = "/profile", method= RequestMethod.GET)
	 // public ProfileDetailResponseDTO profile() {
	 public AuthUserDto profile() {
		User subject = this.getUser();
	
		AuthUserDto authUserDetails = AuthUserDto.buildFromUser(subject);

		// [ Quick Profile query ] --------------------
		//ProfileDetailResponseDTO response = new ProfileDetailResponseDTO(user.username);
		return authUserDetails;
		
	  }
	@RequestMapping(method = RequestMethod.POST, value = "authenticate")
	public AuthResponseDto authenticate(@Validated @RequestBody AuthRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
		User subject = userService.getUser(dto.getLoginId());
		return login(dto, subject,request, response);
	}
	private AuthResponseDto login(AuthRequestDto dto,User subject, HttpServletRequest request,
			HttpServletResponse response) {
		 if (subject == null) {
			logger.info("Non-active status for user: {}",  dto.getLoginId());
			return new AuthResponseDto(false, Codes.AuthenticationResponse.USER_INACTIVE, Messages.Errors.LOGIN_NO_ACCESS,null);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String currentDtInStr = formatter.format(DateUtil.currentDate());
		
		TokenAuthenticationService.addAuthentication(request, response, subject);
		AuthUserDto authUserDetails = AuthUserDto.buildFromUser(subject);
		String reason = "Login info verified.";

		return new AuthResponseDto(true, Codes.AuthenticationResponse.AUTHENTICATED, reason, authUserDetails);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "logout")
	public void logout() {
//		if(getUser() != null) {
//			updateLoginLog( getUser().getUsername(), getUser().getLastLoginIp(),Codes.LoginType.PASSWORD,Codes.LoginStatus.LOGOUT);
//		}
		setInAuthenticated();
	}
}
