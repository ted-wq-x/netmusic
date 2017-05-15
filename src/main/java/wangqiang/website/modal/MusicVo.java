package wangqiang.website.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by wangq on 2017/5/15.
 */
@Entity(name = "netmusic")
public class MusicVo {

    @Id
    private Integer id;

    @Column(nullable = false, name = "commit_total")
    private Integer commitTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCommitTotal() {
        return commitTotal;
    }

    public void setCommitTotal(Integer commitTotal) {
        this.commitTotal = commitTotal;
    }

    @Override
    public String toString() {
        return "MusicVo{" +
                "id=" + id +
                ", commitTotal=" + commitTotal +
                '}';
    }
}
