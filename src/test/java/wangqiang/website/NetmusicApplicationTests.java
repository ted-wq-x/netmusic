package wangqiang.website;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wangqiang.website.modal.CommentsVo;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.modal.UserVo;
import wangqiang.website.service.CommentsRepository;
import wangqiang.website.service.MusicRepository;
import wangqiang.website.service.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetmusicApplicationTests {

	@Autowired
	private MusicRepository musicRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentsRepository commentsRepository;
	@Test
	public void contextLoads() {
		CommentsVo commentsVo = new CommentsVo();
		commentsVo.setId(1);
		commentsVo.setUserId(482258385);
		commentsVo.setTime(1174511619);
		commentsVo.setSongId(4083399);commentsVo.setContent("\uD83C\uDF1D初一王强……");
		commentsRepository.save(commentsVo);
	}

}
