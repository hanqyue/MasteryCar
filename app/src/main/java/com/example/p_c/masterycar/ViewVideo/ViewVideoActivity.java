package com.example.p_c.masterycar.ViewVideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p_c.masterycar.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewVideoActivity extends Activity implements OnItemClickListener{

    private String cur_path="/storage/emulated/0/行车记录仪/";
    private List<Picture> listPictures;
    ListView listView ;
    private ImageView btnBack;
    private TextView txtTitle;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (msg.what == 0) {
                List<Picture> listPictures = (List<Picture>) msg.obj;
//				Toast.makeText(getApplicationContext(), "handle"+listPictures.size(), 1000).show();
                MyAdapter adapter = new MyAdapter(listPictures);
                listView.setAdapter(adapter);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewvideo);
        inittitle();
        loadVaule();
    }

    private void inittitle(){
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setText("行车记录");
        txtTitle.setVisibility(View.VISIBLE);

    }
    private void loadVaule(){
        File file = new File(cur_path);
        File[] files  = null;
        files = file.listFiles();
        if(file.isDirectory()) {
            listPictures = new ArrayList<Picture>();
            for (int i = 0; i < files.length; i++) {
                Picture picture = new Picture();
                picture.setBitmap(getVideoThumbnail(files[i].getPath(), 200, 200, MediaStore.Images.Thumbnails.MICRO_KIND));
                picture.setPath(files[i].getPath());
                listPictures.add(picture);

            }
            listView = (ListView) findViewById(R.id.lv_show);
            listView.setOnItemClickListener(this);
            Message msg = new Message();
            msg.what = 0;
            msg.obj = listPictures;

            handler.sendMessage(msg);
        }
        else {
            Toast.makeText(this,"NO",Toast.LENGTH_LONG);
        }

    }


    //获取视频的缩略图
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }





    public class MyAdapter extends BaseAdapter {
        private List<Picture> listPictures;

        public MyAdapter(List<Picture> listPictures) {
            super();
            this.listPictures = listPictures;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listPictures.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return listPictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup arg2) {
            // TODO Auto-generated method stu
            View view = getLayoutInflater().inflate(R.layout.videoitem,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_show);
            TextView textView = (TextView) view.findViewById(R.id.tv_show);

            imageView.setImageBitmap(listPictures.get(position).getBitmap());
            textView.setText(listPictures.get(position).getPath().replace(cur_path,"视频名称: "));
            return view;

        }
    }





    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {

        playVideo(listPictures.get(arg2).getPath());
    }

    //调用系统播放器   播放视频
    private void playVideo(String videoPath){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(videoPath);
        intent.setDataAndType(Uri.fromFile(file), "video/mp4");
        startActivity(intent);
    }


}
