package wangqiang.website.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import wangqiang.website.modal.UserVo;

/**
 * Created by wangq on 2017/5/26.
 */
@Service
public interface UserRepository extends JpaRepository<UserVo,Integer> {
}
