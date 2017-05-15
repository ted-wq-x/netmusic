package wangqiang.website.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import wangqiang.website.modal.MusicVo;

/**
 * Created by wangq on 2017/5/15.
 */
@Service
public interface MusicRepository extends JpaRepository<MusicVo,Integer> {
}
