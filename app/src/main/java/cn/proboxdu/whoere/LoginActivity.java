package cn.proboxdu.whoere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Probox on bg2017/4/20.
 */

public class LoginActivity extends AppCompatActivity{
    private SharedPreferences sp=null;
    private EditText edit_name,edit_pass;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//右上角菜单
        getMenuInflater().inflate(R.menu.exit, menu);
        //menu.add(1, Menu.FIRST, 1, "Change Site ID");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//菜单选项
        switch(item.getItemId()){
            case R.id.quit1:
                super.finish();
                System.exit(0);
                return true;
            case R.id.help:
                new AlertDialog.Builder(LoginActivity.this).setTitle("Help")//设置对话框标题
                        .setMessage("Registration specification\n"
                                   +"Name(6-16): alphabet, number, underline.\n"
                                   +"Password(6-16): alphabet, number, underline, decimal point.\n")//设置显示的内容
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                //
                            }
                        }).show();//在按键响应事件中显示此对话框
                return true;
            default:
                return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edit_name = (EditText)findViewById(R.id.name);
        edit_pass = (EditText)findViewById(R.id.password);
        Button but_login = (Button)findViewById(R.id.button_login);
        Button but_register = (Button)findViewById(R.id.button_signup);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sp.getBoolean("Save_Ischeck",true))
        {
            edit_name.setText(sp.getString("username",""));
            edit_pass.setText(sp.getString("password",""));
        }
        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString();
                String pass = edit_pass.getText().toString();
                if (name.length()<6||name.length()>16)
                {
                    Toast.makeText(getApplicationContext(),"ERROR:The length of name should be from 6 to 16.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.length()<6||pass.length()>16)
                {
                    Toast.makeText(getApplicationContext(),"ERROR:The length of password should be from 6 to 16.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Client.UpdateAccount(new AccountUtils(name,pass,"","","","",""))) {
                    Toast.makeText(getApplicationContext(), "ERROR:Incorrect username or password.", Toast.LENGTH_LONG).show();
                    return;
                }
                setProperty(name,pass);
                new_MainActivity(name,pass);
            }
        });
        but_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString();
                String pass = edit_pass.getText().toString();
                if (name.length()<6||name.length()>16)
                {
                    Toast.makeText(getApplicationContext(),"ERROR:The length of name should be from 6 to 16.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.length()<6||pass.length()>16)
                {
                    Toast.makeText(getApplicationContext(),"ERROR:The length of password should be from 6 to 16.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Client.AddAccount(new AccountUtils(name,pass,"1","stud","0.0","0.0","127.0.0.1"))) {
                    Toast.makeText(getApplicationContext(), "ERROR:The user name "+name+" has already existed", Toast.LENGTH_LONG).show();
                    return;
                }
                setProperty(name,pass);
                new_MainActivity(name,pass);
            }
        });
    }
    private void setProperty(String name,String pass)//记住密码
    {
        CheckBox save_password = (CheckBox) findViewById(R.id.save_passwd);
        if (save_password.isChecked()){
            Editor editor =sp.edit();
            editor.putString("username",name);
            editor.putString("password",pass);
            editor.putBoolean("Save_Ischeck", true);
            editor.apply();
        }else{
            Editor editor =sp.edit();
            editor.putString("username",null);
            editor.putString("password",null);
            editor.putBoolean("Save_Ischeck", false);
            editor.apply();
        }
    }
    private void new_MainActivity(String name,String pass) //登录跳转
    {
        Intent intent =new Intent(this,MainActivity.class);
        Bundle bundle=new Bundle();
        //传递参数 name,password
        bundle.putString("name",name);
        bundle.putString("password",pass);
        intent.putExtras(bundle);
        this.startActivity(intent);
        //finish();
    }

}