package com.example.administrator.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.notebook.adapter.NotepadAdapter;
import com.notebook.bean.NotepadBean;
import com.notebook.database.SQLiteHelper;

import java.util.List;



public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<NotepadBean>list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于显示记录的列表
        listView=(ListView)findViewById(R.id.listview);
        ImageView add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        initData();
    }
    protected  void initData(){
        mSQLiteHelper=new SQLiteHelper(this);//创建数据库
        showQueryData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotepadBean notepadBean=list.get(position);
                Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                intent.putExtra("id",notepadBean.getId());
                intent.putExtra("time",notepadBean.getNotepadTime());
                //记录的内容
                intent.putExtra("content",notepadBean.getNotepadContent());
                //跳转到修改记录界面
                MainActivity.this.startActivityForResult(intent,1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否删除此记录")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                NotepadBean notepadBean =list.get(position);
                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();//更新记事本界面
                                    Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }
                            }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();//关闭对话
                            }
                        });
                dialog=builder.create();
                dialog.show();
                return true;



            }
        });
    }
    private void showQueryData(){
        if(list!=null){
            list.clear();
        }
        //从数据库中查询数据
        list=mSQLiteHelper.query();
        adapter=new NotepadAdapter(this,list);
        listView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==2){
            showQueryData();
        }
    }
}
