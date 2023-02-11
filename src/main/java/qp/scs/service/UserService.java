package qp.scs.service;

import static com.google.common.base.Objects.equal;

import org.springframework.stereotype.Service;

import qp.scs.dto.request.LoginRequestDTO;
import qp.scs.dto.response.LoginResponseDTO;
import qp.scs.model.User;
import qp.scs.model.api.Entity;
import qp.scs.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.joda.time.Period;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;


@Service
@Transactional
public class UserService extends BaseService {

	private static Integer expireTokenHrs;
	
	private static Integer expireTokenMins;
	
	private final AtomicLong counter = new AtomicLong();

	@Autowired // This means to get the bean called userRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;

	
	@Value("${token.expiry.hours}")
	public void setExpireTokenHrs(Integer expireTokenHrs) {
		UserService.expireTokenHrs = expireTokenHrs;
	}
	
	@Value("${token.expiry.minutes}")
	public void setExpireTokenMins(Integer expireTokenMins) {
		UserService.expireTokenMins = expireTokenMins;
	}
	

	

	public void export(XSSFWorkbook workbook, HttpServletResponse response, String contentType) throws IOException {
		workbook.write(response.getOutputStream());
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment");
		response.flushBuffer();
		workbook.close();
	}
	
	public User getUser(String username) {
		
		User u = userRepository.getUserByUserName(username);

		
		return u;
	}
}
