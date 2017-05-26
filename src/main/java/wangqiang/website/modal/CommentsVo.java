package wangqiang.website.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by wangq on 2017/5/26.
 */
@Entity(name = "music_comments")
public class CommentsVo {

    @Id
    private Integer id;

    @Column(name = "liked_count")
    private Integer likedCount;

    @Column(name = "content")
    private String content;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "time")
    private Integer time;

    @Column(name = "song_id", nullable = false)
    private Integer songId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(Integer likedCount) {
        this.likedCount = likedCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    @Override
    public String toString() {
        return "CommentsVo{" +
                "id=" + id +
                ", likedCount=" + likedCount +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", time=" + time +
                ", songId=" + songId +
                '}';
    }
}
