package com.qrcode;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

public class MainActivity extends Activity {
	private TextView resultTextView;
	private EditText qrStrEditText;
	private ImageView qrImgImageView;
	public  Bitmap portrait;
	public  String url;//网址
	private static final int PORTRAIT_SIZE = 65;//二维码中间图片的大小
	public Bitmap qrCodeBitmap ;
	int widthAndHeight = 500;//生成二维码的大小
	private String msg ;
	private String scanResult;//输出的字符串信息
	private boolean paramBoolean;
	
	public  Bitmap initProtrait(String url) {
		try {
			// 这里采用从asset中加载图片
			Bitmap portrait = BitmapFactory.decodeStream(getAssets().open(url));
			// 对原有图片压缩显示大小
			Matrix mMatrix = new Matrix();
			float width = portrait.getWidth();
			float height = portrait.getHeight();
			mMatrix.setScale(PORTRAIT_SIZE / width, PORTRAIT_SIZE / height);
			return Bitmap.createBitmap(portrait, 0, 0, (int) width,
					(int) height, mMatrix, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
        
        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
			}
		});
        
        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String contentString = qrStrEditText.getText().toString();
					if (!contentString.equals("")) {
						//生成加logo的二维码	
						portrait = initProtrait("logo.png");
						qrCodeBitmap = EncodingHandler.createQRCode(contentString, 500,portrait);
						qrImgImageView.setImageBitmap(qrCodeBitmap);
							
						int portrait_W = portrait.getWidth();
						int portrait_H = portrait.getHeight();
						// 设置头像要显示的位置，即居中显示
						int left = (widthAndHeight - portrait_W) / 2;
						int top = (widthAndHeight - portrait_H) / 2;
						int right = left + portrait_W;
						int bottom = top + portrait_H;
						Rect rect1 = new Rect(left, top, right, bottom);

						// 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
						Canvas canvas = new Canvas(qrCodeBitmap);

						// 设置我们要绘制的范围大小，也就是头像的大小范围
						Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
						// 开始绘制
						canvas.drawBitmap(portrait, rect2, rect1, null);
					}else {
						Toast.makeText(MainActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
					}
					
				} catch (WriterException e) {
					e.printStackTrace();
				}
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		if (resultCode == RESULT_OK) {
			//如果字符串中有网址，则用浏览器打开
			Bundle bundle = data.getExtras();
			scanResult = bundle.getString("result");

			    String regex = "(?<=http://)\\S+";//利用正则表达式判断字符串中是否有网址

			    Pattern pattern = Pattern.compile(regex);
			    String candidate = scanResult;
			    Matcher matcher = pattern.matcher(candidate);

			    while (matcher.find()) {
			     msg =matcher.group().toString() ;
				    String ee =  "http://"+msg;
				      Uri uri = Uri.parse(ee); 
				      Intent it  = new Intent(Intent.ACTION_VIEW,uri); 
				      startActivity(it);
			  }
			  //如果字符串中没有网址，则用文本阅读器打开
       if(msg == null)
       {
           Intent intent = new Intent(Intent.ACTION_SEND);  
           intent.setType("text/plain");  
           intent.putExtra(Intent.EXTRA_TEXT, scanResult);//文本内容  
           startActivity(intent);//弹出的选择程序处理的文字标题  
       }
			    	  
		}
	}
}