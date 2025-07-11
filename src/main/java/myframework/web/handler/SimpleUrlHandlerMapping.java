package myframework.web.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;
import myframework.web.servlet.Controller;

@Slf4j
public class SimpleUrlHandlerMapping implements HandlerMapping {

	JsonObject root; // DispatcherServlet이 생성한, Json 매핑 파일을 파싱(해석)한 결과 객체

	// 하위 컨트롤러들을 key-value의 쌍으로 보관해놓자
	// 그래야 요청이 들어올 때, 해당 요청에 동작할 하위 컨트롤러를 DispatcherServlet에게 반환할 것이기 때문
	// Map<url,하위 컨트롤러>
	Map<String, Controller> controllerMap = new HashMap<>(); // 인터페이스는 new안되므로 구현체를 new 하자

	@Override
	public void setRoot(JsonObject root) {
		this.root = root;
		log.debug("DispatcherServlet으로부터 넘겨받은 root는 " + root);
	}

	@Override
	public void initialize() {
		// root를 이용하여, json의 controllerMappings 검색
		JsonObject controllerMappings = root.getAsJsonObject("controllerMappings");
		log.debug("controllerMappings의 결과는 " + controllerMappings);

		// 1. 반복문으로 객체의 모든 키 값에 매핑된 클래스명을 대상으로 인스턴스화 작업 시도
		// 2. controllerMap에 수집해놓기
		// for(Map.Entry<K, V>:controllerMappings.entrySet()) {}

		// 풀어서 반복문 돌리는 버전
		Set set = controllerMappings.keySet(); // .entrySet()은 키:값 다 나옴, .keySe()은 키값만 나옴
		Iterator<String> it = set.iterator();
		
		while (it.hasNext()) { // 요소가 존재하는 동안
			String uri = it.next();
			log.debug("요소는 " + uri);

			String controllerName =	controllerMappings.get(uri).getAsString();
			log.debug("컨트롤러 명은 " + controllerName);
			
			try {
				Controller controller = (Controller)Class.forName(controllerName).newInstance();
				controllerMap.put(uri, controller);
				
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public Controller getController(String uri) {
		return null;
	}

}
