# Pyeo

> Week2 4분반 Team Turtle


* 책상 앞에 오래 앉아있는 현대인들을 위한 Android 어플리케이션입니다.
* 일하기 싫은 직장인들과 공부하기 싫은 학생들에게 커뮤니티를 제공합니다.
* 조금이라도 거북목이 될 낌새가 보이면 알람을 울립니다.
* 올바른 자세를 유지하기 위한 스트레칭 영상을 제공합니다.

![로고, 로그인](https://github.com/chojaework/MadCamp-Week2/assets/121816472/a8509543-1050-404b-84e0-5ebd5ba9e9c2)

***
### 개발 팀원
* 고려대학교 컴퓨터학과 [조재원](https://github.com/chojaework)
* KAIST 전산학부 [차민호](https://github.com/Cha-Minho)
***

### 개발 환경
* OS: Android (minSdk: 24, targetSdk: 34)
* Language: Java
* IDE: Android Studio
* [Server: Django](https://github.com/Cha-Minho/week2_server)
* Database: Mysql
* Target Device: Galaxy S10
***

### 어플리케이션 소개
### Tab 1 - 커뮤니티

![Community](https://github.com/chojaework/MadCamp-Week2/assets/121816472/7089d9ff-e2e9-436f-9b61-62e0ebc81b9e)


#### 주요 기능
* 시간 빌게이츠들을 위한 자유게시판을 제공합니다.
  * 로그인 정보를 바탕으로 게시글을 남기고 댓글을 작성할 수 있습니다.
  * 게시글의 수정/삭제, 댓글의 삭제를 지원합니다.
***
#### 차후 추가될 기능
* 마이 페이지
* 댓글에 대한 답글
* 키보드 배틀을 위한 실시간 채팅
***

### Tab 2 - 거북목 감지

<img src = "https://github.com/chojaework/MadCamp-Week2/assets/121816472/ff7070ed-9353-43cc-b679-8b64a9ce1107" width="490" height="430">


#### 주요 기능
* 카메라를 통해 사용자의 자세를 감지하여 경고음을 울립니다.
  * 사용자의 어깨, 뒷목, 정수리 세 점을 detect하고 각도를 계산합니다. ([MediaPipe](https://github.com/spmallick/learnopencv/tree/master/Posture-analysis-system-using-MediaPipe-Pose) 사용)
  * 각도가 10도가 넘을 경우 화면에 표시되는 점들을 붉은 색으로 바꾸어 표시하고 경고음을 울립니다.
***
#### 차후 추가될 기능
* 공부/업무에 집중하는 정도를 finetuned Neural Network를 통해 측정
* 공부에 집중한 시간을 Database에 저장
* Database에 저장된 공부 시간을 커뮤니티에 인증
***

### Tab 3 - 스트레칭 영상 제공

<img src = "https://github.com/chojaework/MadCamp-Week2/assets/121816472/73c11da4-e8cd-45ba-a3ac-037f29593946" width="490" height="430">


#### 주요기능
* 목, 어깨가 뻐근할 때 간단하게 할 수 있는 스트레칭 영상들을 제공합니다.
* 영상 아래의 카메라를 통해 내 스트레칭 자세를 점검할 수 있습니다.
***
#### 차후 추가될 기능
* Tab 2에서 사용한 Pose detect model을 사용하여 영상과 사용자 간의 자세 유사성을 제공
* 자세 유사 정도를 추적하여 스트레칭의 완성도 제공
* 더 다양한 영상을 분류하여 제공
***
### 기술 설명
#### 1. 카메라 미리보기 캡처
<details>
  <summary> 코드 보기 </summary>

```java
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder holder;
    Camera camera = null;
    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        //초기화를 위한 메소드
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //만들어지는시점
        camera  = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//카메라 객체 참조
        try{
            camera.setPreviewDisplay(holder);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //변경
        camera.startPreview(); //렌즈로 부터 들어오는 영상을 뿌려줌
        camera.stopPreview();
        camera.setDisplayOrientation(90);//카메라 미리보기 오른쪽 으로 90 도회전
        camera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //소멸
        camera.stopPreview();//미리보기중지
        camera.release();
        camera = null;
    }

    public boolean capture(Camera.PictureCallback callback){
        if(camera != null){
            camera.takePicture(null,null,callback);
            return true;
        }
        else{
            return false;
        }
    }
}
```

</details>

#### 2. 유튜브 영상 로더
<details>
  <summary> 코드 보기 </summary>

    YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
    getLifecycle().addObserver(youTubePlayerView);
  
    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
        @Override
        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
            super.onReady(youTubePlayer);
            String videoId = "S0Q4gqBUs7c";
            youTubePlayer.loadVideo(videoId, 0);
        }
    });
    
</details>

#### 3. Custom BottomNavigationView
<details>
  <summary> 코드 보기 </summary>

  ```java
  public class CustomBottomNavigationView extends BottomNavigationView {
      private Path mPath = new Path();
      private Paint mPaint = new Paint();
  
      private static final int CURVE_CIRCLE_RADIUS = 190 / 2;
  
      private Point mFirstCurveStartPoint = new Point();
      private Point mFirstCurveEndPoint = new Point();
      private Point mFirstCurveControlPoint1 = new Point();
      private Point mFirstCurveControlPoint2 = new Point();
  
      private Point mSecondCurveStartPoint = new Point();
      private Point mSecondCurveEndPoint = new Point();
      private Point mSecondCurveControlPoint1 = new Point();
      private Point mSecondCurveControlPoint2 = new Point();
  
      private int mNavigationBarWidth = 0;
      private int mNavigationBarHeight = 0;
  
      public CustomBottomNavigationView(Context context) {
          super(context);
          init();
      }
  
      public CustomBottomNavigationView(Context context, AttributeSet attrs) {
          super(context, attrs);
          init();
      }
  
      public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
          super(context, attrs, defStyleAttr);
          init();
      }
  
      private void init() {
          mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
          mPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
          setBackgroundColor(Color.TRANSPARENT);
      }
  
      @Override
      protected void onSizeChanged(int w, int h, int oldw, int oldh) {
          super.onSizeChanged(w, h, oldw, oldh);
  
          mNavigationBarWidth = getWidth();
          mNavigationBarHeight = getHeight();
  
          mFirstCurveStartPoint.set(mNavigationBarWidth / 2 - CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS / 3, 0);
          mFirstCurveEndPoint.set(mNavigationBarWidth / 2, CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4);
          mSecondCurveStartPoint = mFirstCurveEndPoint;
          mSecondCurveEndPoint.set(mNavigationBarWidth / 2 + CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS / 3, 0);
  
          mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4, mFirstCurveStartPoint.y);
          mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x - CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS, mFirstCurveEndPoint.y);
  
          mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x + CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS, mSecondCurveStartPoint.y);
          mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4), mSecondCurveEndPoint.y);
  
          mPath.reset();
          mPath.moveTo(0F, 0F);
          mPath.lineTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y);
  
          mPath.cubicTo(mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y, mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y, mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);
  
          mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y, mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y, mSecondCurveEndPoint.x, mSecondCurveEndPoint.y);
  
          mPath.lineTo(mNavigationBarWidth, 0F);
          mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
          mPath.lineTo(0F, mNavigationBarHeight);
          mPath.close();
      }
  
      @Override
      protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);
          canvas.drawPath(mPath, mPaint);
      }
  }
  ```

</details>
