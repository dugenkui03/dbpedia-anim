package invoke.invoker;

import lombok.Data;

/**
 * @author 杜艮魁
 * @date 2018/10/11
 */
@Data
public class InvokerBuilder {
    private String localDir;
    private String netAdd;

    public InvokerBuilder localDir(String localDir) {
        this.localDir = localDir;
        return this;
    }

    public InvokerBuilder netAdd(String netAdd) {
        this.netAdd = netAdd;
        return this;
    }

    public KnowledgeBaseInvoker build() {
        return new KnowledgeBaseInvoker(this);
    }
}
