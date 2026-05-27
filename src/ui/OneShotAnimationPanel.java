package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import utils.SoundManager;
/**
 * 一个专门用来播放一次性PNG序列动画的面板。
 * 比如：胜利动画、消除特效、按钮出现动画等。
 */
public class OneShotAnimationPanel extends JPanel {

    private List<Image> frames = new ArrayList<>();
    private int currentFrame = -1; // -1 表示动画未开始
    private Timer timer;

    private int triggerSFX=-1;
    private String sfxName;
    private Runnable onComplete; // 动画播放完毕后要执行的“回调”指令


    public OneShotAnimationPanel(String imagePrefix, int frameCount, int delay) {
        setOpaque(false); // 必须是透明的！

        // 加载所有动画帧
        for (int i = 0; i < frameCount; i++) {
            // 注意：你的 Resprite 导出如果是从 0 开始，这里就用 i
            // 如果是从 1 开始，就用 i + 1
            frames.add(new ImageIcon("resource/images/" + imagePrefix + i + ".png").getImage());
        }
        
        // 创建定时器
        timer = new Timer(delay, e -> {
            currentFrame++;
            if(triggerSFX==currentFrame&&sfxName!=null) {
                SoundManager.playSFX(sfxName);
            }
            if (currentFrame >= frames.size()) {
                // 动画播完了
                timer.stop();

                if (onComplete != null) {
                    onComplete.run(); // 执行“动画播完了该干啥”的指令
                }
                this.setVisible(false); // 播放完毕后隐藏自己
            }
            repaint(); // 刷新画面，绘制下一帧
        });
    }
    public void applySFX(int startcount,String sfxName) {
        triggerSFX=startcount;
        this.sfxName=sfxName;
    }

    /**
     * 开始播放动画
     * @param onCompleteCallback 动画播放完毕后要执行的操作
     */
    public void play(Runnable onCompleteCallback) {
        this.onComplete = onCompleteCallback;
        this.currentFrame = 0;
        this.setVisible(true); // 开始播放时让自己可见
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 如果动画正在播放，就绘制当前帧
        if (currentFrame >= 0 && currentFrame < frames.size()) {
            g.drawImage(frames.get(currentFrame), 0, 0, getWidth(), getHeight(), this);
        }
    }
}