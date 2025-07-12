package myframework.staff.model.service;

import org.apache.ibatis.session.SqlSession;

import lombok.extern.slf4j.Slf4j;
import myframework.mybatis.MybatisConfig;
import myframework.staff.model.domain.Bio;
import myframework.staff.model.repository.BioDAO;
import myframework.staff.model.repository.StaffDAO;

@Slf4j
public class StaffService {
	
	MybatisConfig config = MybatisConfig.getInstance();
	
	StaffDAO staffDAO=new StaffDAO();
	BioDAO bioDAO=new BioDAO();

	public void regist(Bio bio) {
		SqlSession sqlSession=config.getSqlSession();

		try {
			log.debug("사원 등록 전의 staff "+bio.getStaff().getStaff_id());
			staffDAO.insert(sqlSession,bio.getStaff());
			log.debug("사원 등록 후의 staff "+bio.getStaff().getStaff_id());
			
			bioDAO.insert(sqlSession,bio); // bioDAO 호출
			log.debug("41번째줄 테스트");
			sqlSession.commit(); // 모든 DAO 작업 완료 후 커밋
			
		} catch (Exception e) {
			log.error("사원 등록 중 오류 발생", e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
		
	}
	
}
