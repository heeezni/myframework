package myframework.staff.model.repository;

import org.apache.ibatis.session.SqlSession;

import lombok.extern.slf4j.Slf4j;
import myframework.exception.BioException;
import myframework.mybatis.MybatisConfig;
import myframework.staff.model.domain.Bio;
@Slf4j
public class BioDAO {

	MybatisConfig config=MybatisConfig.getInstance();
	
	
	public void insert(SqlSession sqlSession,Bio bio) throws BioException{
		log.debug("bio 객체 상태: {}", bio);

		int result=sqlSession.insert("Bio.insert", bio);
		if(result<1) {
			throw new BioException("Staff의 신체정보 등록 실패");
		}

	}
	
}