package myframework.shop.notice.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import myframework.web.servlet.Controller;

@Slf4j
public class ListController implements Controller {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("목록 요청 받음");
		
	}

	@Override
	public String getViewName() {
		return "/shop/notice/list/view";
	}
	
	@Override
	public boolean isForward() {
		return true;
	}

}
