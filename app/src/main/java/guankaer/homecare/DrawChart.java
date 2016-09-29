package guankaer.homecare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawChart extends View {

	private int CHARTH = 220; // height of chart
	private int CHARTW = 1600; // width of chart
	private int OFFSET_LEFT = 40; // distance between left and chart
	private int OFFSET_TOP = 10; // distance between top and chart
	private int TEXT_OFFSET = 20; // offset of text
	private int X_INTERVAL = 8; // X distance between point and point
	private int X_SAMENUM = 2;
	private int POINTNUM = X_SAMENUM * (CHARTW / X_INTERVAL); // max point
	private int i=0;
	private int oldTarget = 0;//the target value should not be the same.

	private List<Point> plist = new ArrayList<Point>();
	private List<Integer> Rlist = new ArrayList<Integer>();

	public DrawChart(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public void SetWH(int W,int H){
		CHARTH=H;
		CHARTW=W;
		POINTNUM = X_SAMENUM * (CHARTW / X_INTERVAL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (plist.size() == 0)
			drawTable(canvas);
		drawCurve(canvas);
	}

	private void drawTable(Canvas canvas) {

		Paint paint = new Paint();
		paint.setColor(Color.rgb(172,208,82));
		paint.setStyle(Paint.Style.STROKE);
		// paint.setStrokeWidth(Pain);
		paint.setStrokeWidth(2);
		Rect chartRec = new Rect(OFFSET_LEFT, OFFSET_TOP, CHARTW + OFFSET_LEFT,
				CHARTH + OFFSET_TOP);
		canvas.drawRect(chartRec, paint);

		Path textPath = new Path();
		paint.setStyle(Paint.Style.FILL);
		textPath.moveTo(30, 320);
		textPath.lineTo(30, 200);
		paint.setTextSize(15);
		paint.setAntiAlias(true);
		// canvas.drawTextOnPath("Voltage [V]", textPath, 0, 20, paint);

		for (int i = 0; i <= 5; i++) {
			paint.setColor(Color.rgb(180,181,181));

			canvas.drawText("" + 0.5 * i, OFFSET_LEFT - TEXT_OFFSET - 5,
					OFFSET_TOP + CHARTH / 10 * (5 - i)+5, paint);
			if (i > 0)
				canvas.drawText("-" + 0.5 * i, OFFSET_LEFT - TEXT_OFFSET - 10,
						OFFSET_TOP + CHARTH / 10 * (5 + i)+5, paint);
		}

		Path path = new Path();
		PathEffect effects = new DashPathEffect(new float[] { 2, 2, 2, 2 }, 1);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(false);
		paint.setPathEffect(effects);
		for (int i = 1; i < 10; i++) {
			path.moveTo(OFFSET_LEFT, OFFSET_TOP + CHARTH / 10 * i);
			path.lineTo(OFFSET_LEFT + CHARTW, OFFSET_TOP + CHARTH / 10 * i);
			canvas.drawPath(path, paint);
		}
	}

//	private void drawCurve(Canvas canvas) {
//
//		Paint paint = new Paint();
//		paint.setColor(Color.rgb(172,208,82));
//		paint.setStyle(Paint.Style.STROKE);
//		if (plist.size() > 1) {
//
//			Path path = new Path();
//			path.moveTo(plist.get(0).x, plist.get(0).y);
//			for (int i = 1; i < plist.size(); i++)
//				path.lineTo(plist.get(i).x, plist.get(i).y);
//
//			canvas.drawPath(path, paint);
//			paint.setStyle(Paint.Style.FILL);
//			paint.setColor(Color.RED);
//			for (int i = 0; i < Rlist.size() ; i++) {
//				int pos = Rlist.get(i);
//				canvas.drawCircle(plist.get(pos).x, plist.get(pos).y, 4, paint);
//			}
//
//		}
//	}
/******optimize by jyycy_000******/
	private void drawCurve(Canvas canvas) {

		Paint paint = new Paint();
		paint.setColor(Color.rgb(172,208,82));
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		if (plist.size() > 1) {

			for (int i = 1; i < plist.size(); i++)
				canvas.drawLine(plist.get(i-1).x,plist.get(i-1).y,plist.get(i).x,plist.get(i).y,paint);


//			paint.setStyle(Paint.Style.FILL);
//			paint.setColor(Color.RED);
//			for (int i = 0; i < Rlist.size() ; i++) {
//				int pos = Rlist.get(i);
//				canvas.drawCircle(plist.get(pos).x, plist.get(pos).y, 4, paint);
//			}

		}
	}

	public void addPlist(int ecgData){
		if (plist.size() == 0 || plist.size() > POINTNUM) {
			plist.clear();
			Rlist.clear();
			//oldTarget = 0;
		}
		int py = (int)((float)ecgData * (float)CHARTH / 4096.0);
		int tempX = (plist.size() / X_SAMENUM) * X_INTERVAL;
		int tempy = CHARTH + OFFSET_TOP - py;
		if (tempy < OFFSET_TOP)
			tempy = OFFSET_TOP;
		else if (tempy > CHARTH + OFFSET_TOP)
			tempy = CHARTH + OFFSET_TOP;

		Point p = new Point(OFFSET_LEFT + tempX, tempy);
		plist.add(p);
	}

	public void prepareLine(int ecgDataTmp) {

		addPlist(ecgDataTmp);
		if(plist.size()%2==0)
			invalidate();
	}

	public void prepareLine2(int gsensorDataTmp) {

		if (plist.size() == 0 || plist.size() > POINTNUM) {
			plist.clear();
			Rlist.clear();
		}

		int py = gsensorDataTmp * CHARTH >> 14;  //4096*2*2
		int tempX = (plist.size() / X_SAMENUM) * X_INTERVAL;
		int tempy = CHARTH/2 + OFFSET_TOP + py;
		if (tempy < OFFSET_TOP)
			tempy = OFFSET_TOP;
		else if (tempy > CHARTH + OFFSET_TOP)
			tempy = CHARTH + OFFSET_TOP;

		Point p = new Point(OFFSET_LEFT + tempX, tempy);
		plist.add(p);
		if(plist.size()%2==0)
			invalidate();
	}

	public void stop(){
		plist.clear();
		Rlist.clear();
	}

	public void redraw(){
		invalidate();
	}

	/**correct the R point**/
	public void prepareRPeak(int ecgRPeakTmp) {
		/*if ((plist.size() - ecgRPeakTmp/2) < 0)
			return;
		int target=plist.size() - ecgRPeakTmp/2;

		for(int i=target;i<plist.size();i++)
			if(plist.get(i).y<plist.get(target).y && i != oldTarget)
				target=i;

		Rlist.add(target);
		oldTarget = target;
		Log.v("target", String.valueOf(target) + "," + (i++) + "," + plist.size());
		*/
		if ((plist.size() - ecgRPeakTmp) < 0)
			return;




		int target=plist.size() - ecgRPeakTmp;




		if(target>16) {
			for (int i = target-16;i < plist.size(); i++)
				if (plist.get(i).y < plist.get(target).y)
					target = i;
		}
		else{
			for (int i = 0; i<plist.size(); i++)
				if (plist.get(i).y < plist.get(target).y)
					target = i;
		}


		Rlist.add(target);



	}

	public void prepareRPeak2(int ecgRPeakTmp,int[] emdResult){

	}

//	public static void onDestroy() {
//		plist = null;
//		rlist = null;
//
//	}
}
