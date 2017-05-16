package wangqiang.website.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import wangqiang.website.modal.MusicVo;

/**
 * Created by wangq on 2017/5/15.
 */
@Service
public interface MusicRepository extends JpaRepository<MusicVo,Integer> {

    @Query(value = "select id  from netmusic ORDER BY id DESC LIMIT 1",nativeQuery = true)
    Integer selectMax();
}
