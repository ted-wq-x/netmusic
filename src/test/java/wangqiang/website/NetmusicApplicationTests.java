package wangqiang.website;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wangqiang.website.modal.MusicVo;
import wangqiang.website.service.MusicRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NetmusicApplicationTests {

	@Autowired
	private MusicRepository musicRepository;
	
	@Test
	public void contextLoads() {
		System.out.println(musicRepository.findMaxCommont());
	}

}
