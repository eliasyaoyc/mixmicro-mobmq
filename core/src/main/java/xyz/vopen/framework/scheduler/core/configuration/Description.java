package xyz.vopen.framework.scheduler.core.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Description} Description for {@link ConfigOption}. Allows providing multiple rich format.
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2020/10/6
 */
public class Description {
  private final List<String> desc;

  private Description(List<String> desc) {
    this.desc = desc;
  }

  public List<String> getDesc() {
    return this.desc;
  }

  public static DescriptionBuilder builder() {
    return new DescriptionBuilder();
  }

  public static class DescriptionBuilder {
    private List<String> desc = new ArrayList<>();
    /**
     * Creates a simple block of text.
     *
     * @param text a simple block of text
     * @return block of text
     */
    public DescriptionBuilder text(String text) {
      this.desc.add(text);
      return this;
    }

    /**
     * Creates description representation.
     * @return
     */
    public Description build(){
      return new Description(this.desc);
    }
  }
}
