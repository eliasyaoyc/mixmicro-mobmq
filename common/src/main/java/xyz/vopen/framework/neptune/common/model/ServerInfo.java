package xyz.vopen.framework.neptune.common.model;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * {@link ServerInfo}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/19
 */
public class ServerInfo {
  /** Unique service id. */
  private @Nonnull Long id;
  /** Unique service name. */
  private @Nonnull String serviceName;
  /** Ip + Port. */
  private @Nonnull String address;
  /** Creation time. */
  private Date gmtCreate;
  /** Update time. */
  private Date gmtUpdate;

  public ServerInfo(@Nonnull Long id, @Nonnull String serviceName, @Nonnull String address, Date gmtCreate, Date gmtUpdate) {
    this.id = id;
    this.serviceName = serviceName;
    this.address = address;
    this.gmtCreate = gmtCreate;
    this.gmtUpdate = gmtUpdate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtUpdate() {
    return gmtUpdate;
  }

  public void setGmtUpdate(Date gmtUpdate) {
    this.gmtUpdate = gmtUpdate;
  }

  // =====================   BUILDER   =====================
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private String serviceName;
    private String address;
    private Date gmtCreate;
    private Date gmtUpdate;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder serviceName(String serviceName) {
      this.serviceName = serviceName;
      return this;
    }

    public Builder address(String address) {
      this.address = address;
      return this;
    }

    public Builder gmtCreate(Date gmtCreate) {
      this.gmtCreate = gmtCreate;
      return this;
    }

    public Builder gmtUpdate(Date gmtUpdate) {
      this.gmtUpdate = gmtUpdate;
      return this;
    }

    public ServerInfo build() {
      return new ServerInfo(id, serviceName, address, gmtCreate, gmtUpdate);
    }
  }
}
