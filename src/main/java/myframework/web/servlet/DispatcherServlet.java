package myframework.web.servlet;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import myframework.web.handler.HandlerMapping;

/**
 * 웹 애플리케이션의 모든 요청을 1차적으로 처리하는 전면 컨트롤러 (관제탑)
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

	JsonObject root; // 설정파일인 .json을 파싱한 Gson의 객체
	HandlerMapping handlerMapping; // 이 핸들러에 동생 인스턴스들이 uri키 값을 가지고 모여있다.

	/**
	 * 이 서블릿이 초기화 될 때, 매핑 파일에 등록된 컨트롤러들만 인스턴스를 생성하여 모아야 하는데, 이 서블릿이 직접하지 않고, 개발자가
	 * 등록한 핸들러 매핑에게 맡김 + 추후 요청 처리할 때도, 어떤 하위 컨트롤러가 동작해야 하는지도 핸들러 매핑이 알아서 분석하여 이
	 * 서블릿에게 반환함
	 * 
	 * 왜? 어제까지는 요청이 들어올 때마다 하위 컨트롤러의 인스턴스를 생성하는 방식이기 때문에 메모리 낭비
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("DispatcherServlet initializing..."); // 초기화 시작 로그 추가
		// 초기화 파라미터 읽기 (설정파일의 위치 얻기)
		String contextConfigLocation = config.getInitParameter("contextConfigLocation");
		// 현재 애플리케이션의 정보 얻기
		ServletContext context = config.getServletContext();
		String realPath = context.getRealPath(contextConfigLocation);

		try (FileReader reader = new FileReader(realPath)) {
			root = JsonParser.parseReader(reader).getAsJsonObject(); // GSON에 있는 JSON 파싱메서드
			// root만 있으면 JSON을 휘어잡을 수 있다!
			log.debug("root=" + root);

			String mappingType = root.get("mappingType").getAsString();
			log.debug("우리가 사용할 핸들러 매핑은 " + mappingType);

			// 동작할 HandlerMapping이 누구인지는 모르지만, 그 패키지를 포함한 클래스명이 mappingType에 들어있으므로,
			// 스트링을 이용한 클래스 로드를 수행할 수 있는 Class.forName()
			Class cls = Class.forName(mappingType);
			handlerMapping = (HandlerMapping) cls.newInstance();
			handlerMapping.setRoot(root);
			handlerMapping.initialize();
			log.info("DispatcherServlet initialized successfully."); // 초기화 성공 로그 추가

		} catch (Exception e) {
			log.error("Error during DispatcherServlet initialization", e); // 초기화 실패 로그 추가
			throw new ServletException("Failed to initialize DispatcherServlet", e); // 예외 다시 던지기
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("doRequest method called."); // doRequest 진입 로그 추가
		// 해당 요청을 처리
		String uri = request.getRequestURI();
		Controller controller = handlerMapping.getController(uri);
		
		log.debug("요청 uri " + uri);
		log.debug("controller is " + controller);
		log.debug("Request object type: {}", request.getClass().getName()); // request 객체 타입 로그 추가

		// 모든 파라미터 이름과 값 출력 (getParameterMap 사용)
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String paramName = entry.getKey();
			String[] paramValues = entry.getValue();
			for (String paramValue : paramValues) {
				log.debug("Parameter Map - {}: {}", paramName, paramValue);
			}
		}


		if (controller != null) {
			log.debug("Executing controller: {}", controller.getClass().getName());
			controller.execute(request, response); // 다형성으로 동작했음
		} else {
			log.warn("No controller found for URI: {}", uri);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No controller found for " + uri);
			return;
		}

		// 하위 컨트롤러로부터 반환받은 view의 이름을 가지고,
		JsonObject viewMappings = root.getAsJsonObject("viewMappings");
		Iterator<String> it = viewMappings.keySet().iterator();
		String viewPage = null;
		while (it.hasNext()) {
			String viewName = it.next();
			if (controller.getViewName().equals(viewName)) {
				viewPage = viewMappings.get(viewName).getAsString();
				break;
			}
		}
		
		if(controller.isForward()) { // 구해온 결과 페이지를 포워딩으로 처리할 경우
			RequestDispatcher dis=request.getRequestDispatcher(viewPage);
			dis.forward(request, response); // 포워딩 발생
			
		}else { // 구해온 결과 페이지를 리다이렉트로 처리할 경우 (라이언트가 결과 페이지를 재접속하게)
			response.sendRedirect(viewPage);
		}
		

	}

}