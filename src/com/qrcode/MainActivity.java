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
	public  String url;//��ַ
	private static final int PORTRAIT_SIZE = 65;//��ά���м�ͼƬ�Ĵ�С
	public Bitmap qrCodeBitmap ;
	int widthAndHeight = 500;//���ɶ�ά��Ĵ�С
	private String msg ;
	private String scanResult;//������ַ�����Ϣ
	private boolean paramBoolean;
	
	public  Bitmap initProtrait(String url) {
		try {
			// ������ô�asset�м���ͼƬ
			Bitmap portrait = BitmapFactory.decodeStream(getAssets().open(url));
			// ��ԭ��ͼƬѹ����ʾ��С
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
						//���ɼ�logo�Ķ�ά��	
						portrait = initProtrait("logo.png");
						qrCodeBitmap = EncodingHandler.createQRCode(contentString, 500,portrait);
						qrImgImageView.setImageBitmap(qrCodeBitmap);
							
						int portrait_W = portrait.getWidth();
						int portrait_H = portrait.getHeight();
						// ����ͷ��Ҫ��ʾ��λ�ã���������ʾ
						int left = (widthAndHeight - portrait_W) / 2;
						int top = (widthAndHeight - portrait_H) / 2;
						int right = left + portrait_W;
						int bottom = top + portrait_H;
						Rect rect1 = new Rect(left, top, right, bottom);

						// ȡ��qr��ά��ͼƬ�ϵĻ��ʣ���Ҫ�ڶ�ά��ͼƬ�ϻ������ǵ�ͷ��
						Canvas canvas = new Canvas(qrCodeBitmap);

						// ��������Ҫ���Ƶķ�Χ��С��Ҳ����ͷ��Ĵ�С��Χ
						Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
						// ��ʼ����
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
			//����ַ���������ַ�������������
			Bundle bundle = data.getExtras();
			scanResult = bundle.getString("result");

			    String regex = "(?<=http://)\\S+";//����������ʽ�ж��ַ������Ƿ�����ַ

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
			  //����ַ�����û����ַ�������ı��Ķ�����
       if(msg == null)
       {
           Intent intent = new Intent(Intent.ACTION_SEND);  
           intent.setType("text/plain");  
           intent.putExtra(Intent.EXTRA_TEXT, scanResult);//�ı�����  
           startActivity(intent);//������ѡ�����������ֱ���  
       }
			    	  
		}
	}
}