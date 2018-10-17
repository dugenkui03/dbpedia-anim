package cons;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 三元组，保存(h,l,t)
 * @author 杜艮魁
 * @date 2018/10/10
 */

@Data
@AllArgsConstructor
public class Triple<H,L,T> {
    public H h;
    public L l;
    public T t;
}
