package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopAgreementBinding;
import com.yuepeng.wxb.ui.activity.PrivacyPolicyActivity;
import com.yuepeng.wxb.utils.PreUtils;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:3/4/21
 * Email:752497253@qq.com
 */
public class AgreementPop extends MyBaseCenterPop<PopAgreementBinding, BasePresenter> {

    private onClickFinish listener;

    public void setOnClickFinishListener(onClickFinish listener){
        this.listener = listener;
    }

    public AgreementPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initTag();
        mBinding.btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PreUtils.putBoolean(PreUtils.KEY_FIRSTLOGIN,false);
                dismiss();
            }
        });
        mBinding.btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onClickFinish();
                }
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_agreement;
    }

    private void initTag() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append("亲爱的用户，感谢您一直以来的支持！\n" +
                "位寻宝一直以来将用户的信息安全与隐私保护视为自己的“生命线”。我们已根据现行法律法规制定了\n" +
                "《位寻宝隐私政策》，并且对于重点内容，我们已经描述在协议中。请您仔细阅读\n\n"+"提示您注意：当您点击“我同意并确认”时，" +
                "视为您已阅读和愿意接受该协议的所有内容，请您理解。当您点击“不同意”时我们将无法为您提供相关产品和服务，请您不要使用并且退出APP。\n");

        //设置部分文字点击事件
        ClickableSpan serviceClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Bundle bundle = new Bundle();
                bundle.putInt("TYPE",0);
                Intent intent = new Intent();
                intent.setClass(getContext(), PrivacyPolicyActivity.class);
                intent.putExtra("bundle",bundle);
                getContext().startActivity(intent);
            }
        };

        style.setSpan(serviceClickableSpan, 63, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBinding.tvContent.setText(style);
        //设置部分文字颜色
        ForegroundColorSpan serviceColorSpan = new ForegroundColorSpan(Color.parseColor("#55D5BC"));
        style.setSpan(serviceColorSpan, 63, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvContent.setText(style);

    }

    public interface onClickFinish{
        void onClickFinish();
    }
}
