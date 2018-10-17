package util.system;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author 杜艮魁
 * @date 2018/4/9
 */
public class TTASLock implements Lock {

    //私有的，防止被其他方法修改；fixme flag=true表示已经被其他锁获取了
    private AtomicBoolean flag=new AtomicBoolean(false);

    @Override
    public void lock() {
        while(true){
            //当其他线程持有锁时，开始自旋
            while(flag.get()){};
            //尝试获取锁，成功则return
            if(flag.compareAndSet(false,true)){
                return;
            }
        }
    }

    @Override
    public void unlock() {
        flag.set(false);
    }

    public boolean isLock(){
        return flag.get();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
