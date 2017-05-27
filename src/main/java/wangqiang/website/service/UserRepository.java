package wangqiang.website.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import wangqiang.website.modal.UserVo;

/**
 * Created by wangq on 2017/5/26.
 */
@Service
public interface UserRepository extends JpaRepository<UserVo, Integer> {

    @Query(value = "select id  from netmusic_user ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Integer selectMaxId();
}
