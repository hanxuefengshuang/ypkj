package com.yuepeng.wxb.ui.activity;


import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.socks.library.KLog;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.GlideUtils;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityMineInformationBinding;
import com.yuepeng.wxb.presenter.UpdateInformationPresenter;
import com.yuepeng.wxb.presenter.view.UpdateDetailView;
import com.yuepeng.wxb.widget.WeChatPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public
class MineInformationActivity extends BaseActivity<ActivityMineInformationBinding, UpdateInformationPresenter> implements View.OnClickListener, UpdateDetailView {

    private File upLoadFile;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected UpdateInformationPresenter createPresenter() {
        return new UpdateInformationPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_information;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).keyboardEnable(true).titleBar(mBinding.title.titlebar);
        UserModel userModel = App.getInstance().getUserModel();
        GlideUtils.load(this,userModel.getHeadImg(),mBinding.avatar, R.mipmap.avatar);
        mBinding.nickName.setText(userModel.getNickName());
        mBinding.title.titlebar.setTitle("我的信息");
    }

    @Override
    protected void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.llAvatar.setOnClickListener(this);
        mBinding.submit.setOnClickListener(this);
    }

    private void opencamera() {
//        new PhotoPickConfig
//                .Builder(this)
//                .imageLoader(new GlideImageLoader())
//                //图片加载方式，支持任意第三方图片加载库
//                .spanCount(4)         //相册列表每列个数，默认为3
//                .pickMode(PhotoPickConfig.MODE_PICK_MORE)          //设置照片选择模式为单选，默认为单选
//                .maxPickSize(1)   //多选时可以选择的图片数量，默认为1张
//                .setMimeType(MimeType.TYPE_IMAGE)     //显示文件类型，默认全部（全部、图片、视频）
//                .showCamera(true)           //是否展示相机icon，默认展示
//                .showOriginal(false)         //是否显示原图按钮，默认显示
//                .startCompression(false)     //是否开启压缩，默认true
////                .selectedMimeType(selectImageList)     //选择后返回的文件（用于判断下次进入是否可展示其他类型文件）
//                .build();
        ImagePicker.withMulti(new WeChatPresenter())
                .mimeTypes(MimeType.ofImage())
                .filterMimeTypes(MimeType.GIF)
                .setSelectMode(SelectMode.MODE_SINGLE)
                //剪裁完成的图片是否保存在DCIM目录下
                //true：存储在DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
                .cropSaveInDCIM(false)
                //设置剪裁比例
                .setCropRatio(1,1)
                //设置剪裁框间距，单位px
                //是否圆形剪裁，圆形剪裁时，setCropRatio无效
                .cropAsCircle()
                .showCamera(true)//显示拍照
                //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
                .cropStyle(CropConfig.STYLE_FILL)
                //设置留白模式下生成的图片背景色，支持透明背景
                .cropGapBackgroundColor(Color.TRANSPARENT)
                .crop(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片剪裁回调，主线程
                        if (items != null && items.size()>0){
                            List<String>photos = new ArrayList<>();
                            photos.add(items.get(0).getCropUrl());
                            lubanPic(photos);
                            GlideUtils.load(mContext,items.get(0).getCropUrl(),mBinding.avatar, R.mipmap.avatar);
                        }
                    }
                });
    }

    private void lubanPic(List<String>photos) {
        Luban.with(this)
                .load(photos)
                .ignoreBy(100)
                .setTargetDir(getPath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        upLoadFile = file;
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("图片压缩失败");
                        upLoadFile = new File(photos.get(0));
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.llAvatar){
            opencamera();
        }
        if (id == R.id.submit){
            if (getEditText(mBinding.nickName).isEmpty()){
                toast("昵称不能为空");
                return;
            }
            if (upLoadFile != null){
//                showProgressDialog();
                mPresenter.uploadImg(upLoadFile);
            }else {
                mPresenter.updateInfoormation("",getEditText(mBinding.nickName));
            }
        }
    }

    @Override
    public void onUploadImgSuccess(String url) {
        mPresenter.updateInfoormation(url,getEditText(mBinding.nickName));
    }

    @Override
    public void onSuccess() {
        showSuccessDialog("修改成功");

        runDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);

    }

    @Override
    public void onError(Throwable e) {
        showException(e);
    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }


    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }
}