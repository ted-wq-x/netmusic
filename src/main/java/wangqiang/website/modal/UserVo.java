package wangqiang.website.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by wangq on 2017/5/26.
 */
@Entity(name = "netmusic_user")
public class UserVo {

    @Id
    private Integer id;

    @Column(nullable = false, name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false,name = "nick_name")
    private String nickname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "id=" + id +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
