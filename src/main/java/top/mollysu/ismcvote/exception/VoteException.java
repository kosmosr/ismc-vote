package top.mollysu.ismcvote.exception;

/**
 * @author zengminghao
 * @date 2020/5/18 9:41
 */
public class VoteException extends RuntimeException {
    private String message;

    public VoteException(String message) {
        super(message);
        this.message = message;
    }

    public VoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
