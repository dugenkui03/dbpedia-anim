package cons;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ��Ԫ�飬����(h,l,t)
 * @author ���޿�
 * @date 2018/10/10
 */

@Data
@AllArgsConstructor
public class Triple<H,L,T> {
    public H h;
    public L l;
    public T t;
}
