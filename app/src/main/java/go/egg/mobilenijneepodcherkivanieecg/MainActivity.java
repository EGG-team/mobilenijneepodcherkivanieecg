package go.egg.mobilenijneepodcherkivanieecg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(getApplicationContext()));
        LinearLayout linearLayout = findViewById(R.id.paint_there);
    }

    class DrawView extends View {

        Paint p;
        Rect rect;

        public DrawView(Context context) {
            super(context);
            p = new Paint();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            // заливка канвы цветом
            p.setColor(Color.RED);
            p.setStrokeWidth(10);


            // рисуем линию от (100,100) до (500,50)
            canvas.drawLine(0,100,49,199,p);

            canvas.drawCircle(50,200,5,p);
            canvas.drawLine(49,199,100,100,p);
            canvas.drawCircle(100,100,5,p);
            canvas.drawLine(100,100,200,200,p);
            canvas.drawCircle(200,200,5,p);
            canvas.drawLine(200,200,220,100,p);

        }
    }



}
