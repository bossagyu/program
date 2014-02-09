//-------------------------------------
//変更内容を記述
//乱数に重みをつける
//飛び4飛び3の検出
//三三の防止強化
//二連に対して近づけて置く ←ランダムに置いたほうが強いので処理を切りました
//四四の判定
//飛び５連の判定、両方に置かれた場合使えないので改良が必要 ←改良完了
//５取りの勝利確定
//五取りの防御
//６個以上石を取ったら3連を作るよりも石とりを重視
//すでに死んでいる３連に対して４連を作らない
//完全な三連を優先的に防御
//完全な四連を優先的に作成
//五連崩し
//四連石取り崩し
//間２連からの三連の作成 ←なぜか弱くなるので処理を切りました
//四三の判定
//石がとれる場合に補正をかける
//取られる形に石を置かない
//飛び三三の作成
//完全な飛び三の検出(攻撃用)
//四三に飛びの判定を追加
//相手が石を規定数以上とると防御行動を優先する
//石がとられる形になる場合は補正をかける

//変更が必要な点
//３連を防止するときに優先順位が必要
//石とりダブルの判定
//飛び四三の検出(攻撃用)
//相手の四三を防ぐ
//相手の飛び三三を防ぐ
//相手の飛び四三を防ぐ
//攻撃用飛び四
package data.strategy.user.s11g492;

import sys.game.GameBoard;
import sys.game.GameCompSub;
import sys.game.GameHand;
import sys.game.GamePlayer;
import sys.game.GameState;
import sys.struct.GogoHand;
import sys.user.GogoCompSub;


// クラス名を変更する
public class User_s11g492_01 extends GogoCompSub {

	
//====================================================================
//  コンストラクタ
//====================================================================

  public User_s11g492_01(GamePlayer player) {  // クラス名を変更する
    super(player);
    name = "s11g492";    // 自分の学籍番号
    
  }
	
//----------------帯域変数---------------
	int diside_i=0;
	int diside_j=0;
//--------------------------------------------------------------------
//  コンピュータの着手
//--------------------------------------------------------------------

	public synchronized GameHand calc_hand(GameState state, GameHand hand) {
		theState = state;
		theBoard = state.board;
		lastHand = hand;
		
		//--  置石チェック
		init_values(theState, theBoard);
		
		//--  評価値の計算
		calc_values(theState, theBoard);
		// 先手後手、取石数、手数(序盤・中盤・終盤)で評価関数を変える
		
		//--  着手の決定
		return deside_hand();
		
	}

//----------------------------------------------------------------
//  置石チェック
//----------------------------------------------------------------

	public void init_values(GameState prev, GameBoard board) {
		this.size = board.SX;
		values = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board.get_cell(i, j) != board.SPACE) {
					values[i][j] = -2;
				} else {
					if (values[i][j] == -2) {
						values[i][j] = 0;
					}
				}
			}
		}
	}
	
//----------------先読み用の評価値の初期化-----------------------
	public void init_values2(int cell[][], int values2[][]) {
		;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(cell[i][j] != 0){
					values2[i][j] = -2;
				}else{
					values2[i][j]=0;
				}
			}
		}
	}


//----------------------------------------------------------------
//  評価値の計算
//----------------------------------------------------------------

	public void calc_values(GameState prev, GameBoard board) {
		int [][] cell = board.get_cell_all();  // 盤面情報
		int mycolor;                  // 自分の石の色
		mycolor = role;
		int mystone=get_mystone(prev);
		int enemystone=get_enemystone(prev);
		int values2[][];
		values2 = new int [size][size];
		int values3[][];
		values3 = new int [size][size];
		int stone_num;
		int stone_sub;
		int values4[][];
		values4 = new int [size][size];
		
		
	
		
		//-------通常一手目-----
		
		 cloc_zyoban_value(cell,mycolor,mystone,enemystone);
		init_values2(cell,values2);
		init_values2(cell,values3);
		init_values2(cell,values4);
		
		//----------補正表の作成------------
		//-------次の局面を作成して手番を入れ替え------
		if(prev.step >6){
			for(int i=0;i<size;i++){
				for(int j=0;j<size;j++){
					cell = board.get_cell_all();						//現状の盤面に初期化
					mycolor = role;										//カラーを初期化
					if(cell[i][j] !=0){continue;}	
					mystone=get_mystone(prev);							//現状の石の数を取得
					stone_num = deside_hand3(cell,values,mycolor,i,j);	//そこに石を置く
					mystone += (stone_num*2);							//石の数を取得
					stone_sub = mystone;								//石の数を入れ替える
					mystone = enemystone;
					enemystone = stone_sub;
					mycolor *=(-1);										//カラーを返る
					values2[i][j]=cloc_enemy_value(cell,mycolor,mystone,enemystone);	//先読み相手
					
					//先読み２手目
					stone_num = deside_hand3(cell,values,mycolor,diside_i,diside_j);	//そこに石を置く
					mystone += (stone_num*2);							//石の数を取得
					stone_sub = mystone;								//石の数を入れ替える
					mystone = enemystone;
					enemystone = stone_sub;
					mycolor *=(-1);										//カラーを返る
					values3[i][j]=cloc_next_value(cell,mycolor,mystone,enemystone);	//先読み自分の二手目
				}
			}
		}
		
		//----通常---
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
			
			}
			
		}
		//-----計算----
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(values2[i][j] == 2){
					//-----指定の行動以外-----3001石取り優先時の石取り　負け確定時の行動
					if(values[i][j] < 9000 && values[i][j] > 2400  && values[i][j] != 3001){
						values4[i][j]=0;
						values4[i][j]+=values3[i][j];
					}else{
						values4[i][j]=values[i][j]+values2[i][j];
						if(values3[i][j] == 600){
							values4[i][j]+=values3[i][j];
						}
					}
					continue;
				}
				if(values2[i][j] == 3){
					if(values[i][j] < 8500 && values[i][j] > 2400 && values[i][j] != 3001){
						values4[i][j]=0;
						if(values[i][j] >1500 && values[i][j] <1600){
							values4[i][j]=2100;
						}else{
							values4[i][j]=values[i][j]+values2[i][j];
							if(values3[i][j] == 600){
								values4[i][j]+=values3[i][j];
							}
						}
						continue;
					}
				}
				values4[i][j]=values[i][j]+values2[i][j];
				values4[i][j]+=values3[i][j];
			}
		}
		int max_val=0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(values4[i][j] > max_val){
					max_val = values4[i][j];
				}
			}
		}
		if(max_val > 0){
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					values[i][j]=values4[i][j];
				}
			}
		}
		
		//-----一手目先読み----
	
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
			
			}
			
		}
		//-----二手目先読み-----
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
			
			}
			
		}
		//-----結果-----
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
			
			}

		}
		
		//-------先読み一手目(相手)------
		//cloc_enemy_value(cell,mycolor,mystone,enemystone,values2);
		
		/*
		//-----次の局面を作成して手番を入れ替え------
		stone_num = deside_hand2(cell,values2,mycolor);
		mystone += (stone_num*2);
		stone_sub = mystone;
		mystone = enemystone;
		enemystone = stone_sub;
		mycolor *=(-1);
		init_values2(cell,values2);
		//-------先読み二手目(自分)-----------------
		cloc_next_value(cell,mycolor,mystone,enemystone,values2);
		*/
	}
//--------------------------------------------------------------------------------
//序盤用評価関数
//-------------------------------------------------------------------------------
	public void cloc_zyoban_value(int cell [][],int mycolor,int mystone,int enemystone){
		
		
		//--  各マスの評価値
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				//フラグ類
				int catched_flag=0;
				int bigren_flag=0;
				int haiboku=0;
				
				// 埋まっているマスはスルー
				if (values[i][j] == -2) { continue; }
				
		// 三々の禁じ手は打たない → -1  sub=1とする
				if(check_run_sp(cell,mycolor,i,j,1)){
					values[i][j]=-1;
					continue;
				}
				
		//石がとれる場合と守る場合に補正をかける
				values[i][j]=10;
				if ( check_run_sp(cell, mycolor, i, j, 12) ) { values[i][j] += 20; }
				if ( check_run_sp(cell, mycolor, i, j, 41) ) { values[i][j] += 12; }
		
		//石に寄せて置く形になるときは補正をかける
				if(check_run_sp(cell,mycolor,i,j,15)){values[i][j] += 10; }
				
		//そこに置かないと相手の三連および四連ができる場合は補正をかけて防ぐ
				if ( check_run_sp(cell, mycolor*(-1), i, j, 43) ) { values[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 44) ) { values[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 7) ) { values[i][j] += 20;}
				if ( check_run_sp(cell, mycolor*(-1), i, j, 17) ) { values[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 3) ) { values[i][j] += 20;}
				
		//そこに置くと三連および四連ができる場合は補正をかける
				if ( check_run_sp(cell,mycolor,i,j,17) ){ values[i][j] += 20; }
				if(check_run_sp(cell,mycolor,i,j,20)){ values[i][j] += 20; }
		
		//完全2連ができる場合は補正をかける
				if(check_run_sp(cell,mycolor,i,j,23)){ values[i][j] += 6; }
				
		//なんか周りに石が多い時は補正をかける
				values[i][j] += (check_run_num(cell,mycolor,i,j,2) /10);
				
		//置いたときに石がとられる形になる場合は補正をかける 行動によっては大きな補正をかけて行動を変更する
				if(check_run_sp(cell,mycolor,i,j,70)){ values[i][j] -= 50; catched_flag=1; }
		//相手の石を置いたときに取られる形に成る場合も補正をかける
				if(check_run_sp(cell,mycolor*(-1),i,j,70)){ values[i][j] -= 30;  }
		//四連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 45)) {haiboku =1; }
				
		//五連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 46)) { haiboku =2;}
		
		// 勝利(五取) → 1000;
				if(mystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 12)) { 
						values[i][j] += 10000;
						continue;
					}
					
				}
				if(mystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 13)) { 
						values[i][j] += 10000;
						continue;
					}
					
				}
				
		// 相手の五連を崩す → 950;
				if ( check_run_sp(cell, mycolor, i, j, 30) ) {
					values[i][j] += 9500;
					continue;
				}
				
				
		// 敗北阻止(五取) → 925;
				if(enemystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 41) ) { 
						values[i][j] += 9500;
						continue;
					}
					
				}
				if(enemystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 42) ) { 
						values[i][j] += 9500;
						continue;
					}
					
				}
				
		//五連の作成 900
				//五連
				if ( check_run_sp(cell, mycolor, i, j, 2) ) {
					values[i][j] += 9000;
					continue;
				}
		//完全四連を石を取って止める
				if ( check_run_sp(cell, mycolor, i, j, 38) ) {
					values[i][j] += 9000;
					continue;
				}
				
		//四連を石を取って止める → 775 sub=11
				
				if ( check_run_sp(cell, mycolor, i, j, 39) ) {
					values[i][j] += 8500;
					continue;
				}
				if ( check_run_sp(cell, mycolor, i, j, 40) ) {
					values[i][j] += 8500;
					continue;
				}
				
		//単純な四連を止める → 750  sub=8
				if ( check_run_sp(cell, mycolor*(-1), i, j, 31) ) {
					values[i][j] += 8000;
					continue;
				}
		
		//四四の作成 → 675 sub=3
				if(check_run_sp(cell,mycolor,i,j,4)){
					values[i][j]+=7000;
					continue;
				}
		//完全な自分の四連を作る →650  sub=7
				if(check_run_sp(cell,mycolor,i,j,3)){
					values[i][j]+=7000;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values[i][j]+=6050;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values[i][j]+=6050;
					continue;
				}
		
		//四三を作成する
				if(check_run_sp(cell,mycolor,i,j,5)){
					values[i][j]+=6000;
					continue;
				}
		//四三を防御する
				if(check_run_sp(cell,mycolor*(-1),i,j,34)){
					values[i][j]+=5500;
					continue;
				}
	
		
		//相手の完全な3連を石を取って崩す
				if ( check_run_sp(cell,mycolor,i,j,36) ) {
					values[i][j] += 5200;
					continue;
				}
		
		//完全な相手の3連を防ぐ → 550 sub=5
				if(check_run_sp(cell,mycolor*-1,i,j,32)){
					values[i][j]+=5050;
					continue;
				}
		//復活三連
				if(check_run_sp(cell,mycolor,i,j,19)){
					values[i][j]+=3050;
					continue;
				}
		//飛び三三を作る → 530 sub=15
				if(check_run_sp(cell,mycolor,i,j,6)){
					values[i][j]+=2500;
					continue;
				}
				
		//石とり複数
				if(check_run_sp(cell, mycolor, i, j, 13)){
					values[i][j]+=3000;
					continue;
				}
		//とった石が６つ以上なら石をとるのを優先 →450
				if(mystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,12) ) { 
						values[i][j] = 3001;
						continue;
					}
					
				}
		//相手の三三を防ぐ
				if(check_run_sp(cell,mycolor*(-1),i,j,35)){
					values[i][j]+=2000;
					continue;
				}
		
		//とられた石が６つ以上なら石を守るのを優先
				if(enemystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,41) ) { 
						values[i][j] += 2000;
						continue;
					}
					
				}
		// 相手の石を取る → 420;
				if ( check_run_sp(cell,mycolor,i,j,12) ) { 
					values[i][j] += 2300;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					if(haiboku >= 1){
						values[i][j] = 7700;
					}
					continue;
				}
		//石ガード複数
				if(check_run_sp(cell, mycolor, i, j, 42)){
					
					values[i][j]+=2000;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					continue;
				}
		
		//自分の完全な三連を作る
				if(check_run_sp(cell,mycolor,i,j,20)){
					values[i][j]+=2050;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					continue;
				}
		
	// 自分の四連を作る補正つき
				if ( check_run_sp(cell,mycolor,i,j,21) ) {
					values[i][j] += 2030;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					continue;
				}
		// 自分の四連を作る → 600;
				if ( check_run_sp(cell,mycolor,i,j,22) ) {
					values[i][j] += 2000;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					
					continue;
				}
		
				
		// 自分の石を守る → 400;
				if ( check_run_sp(cell,mycolor,i,j,41) ) { 
					values[i][j] += 2000;
					if(catched_flag == 1){
						values[i][j]=0;
					}
					continue;
				}
		
				
		//石とり準備複数
				if(check_run_sp(cell,mycolor,i,j,16)){
						values[i][j]+=1700;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					if(haiboku == 1){
						values[i][j] = 7600;
					}
					continue;
				}
		//飛び三連横叩き
				if(check_run_sp(cell,mycolor*(-1),i,j,35)){
					values[i][j]+=1700;
					continue;
				}
				
		//石とり準備
				if(check_run_sp(cell,mycolor,i,j,15)){
						values[i][j]+=1500;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					if(haiboku == 1){
						values[i][j] = 7500;
					}
					continue;
				}
		
		
		//自分の完全な飛び三連を作る
				if(check_run_sp(cell,mycolor,i,j,9)){
					
					values[i][j]+=1800;
					if(catched_flag == 1){
						values[i][j] =0;
					}
					continue;
				}
		
				
		// 相手の三連を防ぐ → 500;
				if ( check_run_sp(cell,mycolor*-1,i,j,43) ) {
					values[i][j] += 1000;
					continue;
				}
				
		// ランダム
				if (values[i][j] <= 10) {
					int aaa = (int) Math.round(Math.random() * 5 +20);
					aaa=aaa-Math.abs(6-i)-Math.abs(6-j);
					
					if (values[i][j] < aaa) { 
						values[i][j] += aaa; 
					}
					values[i][j] += check_run_num(cell,mycolor,i,j,1)*5;
					if(check_run_num(cell,mycolor,i,j,1)>0){
						
					}
				}
				
		
			}
		}
		//-------------------評価値の表示-----------------------
		
		
		
	}

	public void cpy1(int value1[][], int value2[][],int size){
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				value1[i][j]=value2[i][j];
			}
		}
	}
	
//------------------------------------------------------------------------------------------------
//相手の評価値
//-----------------------------------------------------------------------------------------------

	public int cloc_enemy_value(int cell [][],int mycolor,int mystone,int enemystone){
		
		int values2[][];
		values2 = new int [size][size];
		init_values2(cell,values2);
		
		int values1[][];
		values1 = new int [size][size];
		init_values2(cell,values1);
		
		//--  各マスの評価値
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				//フラグ類
				int catched_flag=0;
				int bigren_flag=0;
				int haiboku=0;
				
				// 埋まっているマスはスルー
				if (values2[i][j] == -2) { continue; }
				
		// 三々の禁じ手は打たない → -1  sub=1とする
				if(check_run_sp(cell,mycolor,i,j,1)){
					values2[i][j]=-1;
					continue;
				}
				
		//石がとれる場合と守る場合に補正をかける
				values2[i][j]=10;
				if ( check_run_sp(cell, mycolor, i, j, 12) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor, i, j, 41) ) { values2[i][j] += 12; }
		
		//石に寄せて置く形になるときは補正をかける
				if(check_run_sp(cell,mycolor,i,j,15)){values2[i][j] += 10; }
				
		//そこに置かないと相手の三連および四連ができる場合は補正をかけて防ぐ
				if ( check_run_sp(cell, mycolor*(-1), i, j, 43) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 44) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 7) ) { values2[i][j] += 20;}
				if ( check_run_sp(cell, mycolor*(-1), i, j, 17) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 3) ) { values2[i][j] += 20;}
				
		//そこに置くと三連および四連ができる場合は補正をかける
				if ( check_run_sp(cell,mycolor,i,j,17) ){ values2[i][j] += 20; }
				if(check_run_sp(cell,mycolor,i,j,20)){ values2[i][j] += 20; }
		
		//完全2連ができる場合は補正をかける
				if(check_run_sp(cell,mycolor,i,j,23)){ values2[i][j] += 6; }
				
		//なんか周りに石が多い時は補正をかける
				values2[i][j] += (check_run_num(cell,mycolor,i,j,2) /10);
				
		//置いたときに石がとられる形になる場合は補正をかける 行動によっては大きな補正をかけて行動を変更する
				if(check_run_sp(cell,mycolor,i,j,70)){ values2[i][j] -= 50; catched_flag=1; }
		//相手の石を置いたときに取られる形に成る場合も補正をかける
				if(check_run_sp(cell,mycolor*(-1),i,j,70)){ values2[i][j] -= 30;  }
		//四連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 45)) {haiboku =1; }
				
		//五連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 46)) { haiboku =2;}
		
		// 勝利(五取) → 1000;
				if(mystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 12)) { 
						values2[i][j] += 9000;
						values1[i][j]=1;
						continue;
					}
					
				}
				if(mystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 13)) { 
						values2[i][j] += 9000;
						values1[i][j]=1;
						continue;
					}
					
				}
				
		// 相手の五連を崩す → 950;
				if ( check_run_sp(cell, mycolor, i, j, 30) ) {
					values2[i][j] += 8500;
					values1[i][j]=-100;
					continue;
				}
				
				
		// 敗北阻止(五取) → 925;
				if(enemystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 41) ) { 
						values2[i][j] += 8200;
						values1[i][j]=1000;
						continue;
					}
					
				}
				if(enemystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 42) ) { 
						values2[i][j] += 8200;
						values1[i][j]=1000;
						continue;
					}
					
				}
				
		//五連の作成 900
				//五連
				if ( check_run_sp(cell, mycolor, i, j, 2) ) {
					values2[i][j] += 8100;
					values1[i][j]=2;
					continue;
				}
		//完全四連を石を取って止める
				if ( check_run_sp(cell, mycolor, i, j, 38) ) {
					values2[i][j] += 8000;
					values1[i][j]=-100;
					continue;
				}
				
		//四連を石を取って止める → 775 sub=11
				
				if ( check_run_sp(cell, mycolor, i, j, 39) ) {
					values2[i][j] += 7800;
					values1[i][j]=-900;
					continue;
				}
				if ( check_run_sp(cell, mycolor, i, j, 40) ) {
					values2[i][j] += 7800;
					values1[i][j]=-900;
					continue;
				}
				
		//単純な四連を止める → 750  sub=8
				if ( check_run_sp(cell, mycolor*(-1), i, j, 31) ) {
					values2[i][j] += 7500;
					values1[i][j]=100;
					continue;
				}
		
		//四四の作成 → 675 sub=3
				if(check_run_sp(cell,mycolor,i,j,4)){
					values2[i][j]+=6800;
					values1[i][j]=3;
					continue;
				}
		//完全な自分の四連を作る →650  sub=7
				if(check_run_sp(cell,mycolor,i,j,3)){
					values2[i][j]+=6700;
					values1[i][j]=3;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values2[i][j]+=6500;
					values1[i][j]=-500;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values2[i][j]+=6400;
					values1[i][j]=-500;
					continue;
				}
		
		//四三を作成する
				if(check_run_sp(cell,mycolor,i,j,5)){
					values2[i][j]+=6300;
					values1[i][j]=-900;
					continue;
				}
		
	
		
		//相手の完全な3連を石を取って崩す
				if ( check_run_sp(cell,mycolor,i,j,36) ) {
					values2[i][j] += 5800;
					values1[i][j]=-900;
					continue;
				}
		
		//完全な相手の3連を防ぐ → 550 sub=5
				if(check_run_sp(cell,mycolor*-1,i,j,32)){
					values2[i][j]+=5600;
					values1[i][j]=100;
					continue;
				}
		//復活三連
				if(check_run_sp(cell,mycolor,i,j,19)){
					values2[i][j]+=5400;
					values1[i][j]=-500;
					continue;
				}
		//飛び三三を作る → 530 sub=15
				if(check_run_sp(cell,mycolor,i,j,6)){
					values2[i][j]+=5300;
					values1[i][j]=-600;
					continue;
				}
				
		//石とり複数
				if(check_run_sp(cell, mycolor, i, j, 13)){
					values2[i][j]+=5200;
					values1[i][j]=-400;
					continue;
				}
		//とった石が６つ以上なら石をとるのを優先 →450
				if(mystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,12) ) { 
						values2[i][j] += 5300;
						values1[i][j]=-400;
						continue;
					}
					
				}
		//相手の三三を防ぐ
				if(check_run_sp(cell,mycolor*(-1),i,j,35)){
					values2[i][j]+=5000;
					values1[i][j]=100;
					continue;
				}
		
		//とられた石が６つ以上なら石を守るのを優先
				if(enemystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,41) ) { 
						values2[i][j] += 4800;
						values1[i][j]=0;
						continue;
					}
					
				}
		// 相手の石を取る → 420;
				if ( check_run_sp(cell,mycolor,i,j,12) ) { 
					values2[i][j] += 4900;
					values1[i][j]=-400;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku >= 1){
						values2[i][j] = 7700;
					}
					continue;
				}
		//石ガード複数
				if(check_run_sp(cell, mycolor, i, j, 42)){
					
					values2[i][j]+=4700;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		
		//自分の完全な三連を作る
				if(check_run_sp(cell,mycolor,i,j,20)){
					values2[i][j]+=4650;
					values1[i][j]=-300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		
	// 自分の四連を作る補正つき
				if ( check_run_sp(cell,mycolor,i,j,21) ) {
					values2[i][j] += 4630;
					values1[i][j]=-300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		// 自分の四連を作る → 600;
				if ( check_run_sp(cell,mycolor,i,j,22) ) {
					values2[i][j] += 4590;
					values1[i][j]=-300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					
					continue;
				}
		
				
		// 自分の石を守る → 400;
				if ( check_run_sp(cell,mycolor,i,j,41) ) { 
					values2[i][j] += 4400;
					values1[i][j]=100;
					if(catched_flag == 1){
						values2[i][j]=0;
					}
					continue;
				}
		
				
		//石とり準備複数
				if(check_run_sp(cell,mycolor,i,j,16)){
					values2[i][j]+=4000;
					values1[i][j]=-200;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku == 1){
						values2[i][j] = 7600;
					}
					continue;
				}
				
				
		//石とり準備
				if(check_run_sp(cell,mycolor,i,j,15)){
					values2[i][j]+=3500;
					values1[i][j]=-200;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku == 1){
						values2[i][j] = 7500;
					}
					continue;
				}
		
		
		//自分の完全な飛び三連を作る
				if(check_run_sp(cell,mycolor,i,j,9)){
					
					values2[i][j]+=3000;
					values1[i][j]=200;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		
				
		// 相手の三連を防ぐ → 500;
				if ( check_run_sp(cell,mycolor,i,j,9) ) {
					values2[i][j] += 2000;
					values1[i][j]=0;
					continue;
				}
				
		// ランダム
				if (values2[i][j] <= 10) {
					int aaa = (int) Math.round(Math.random() * 5 +20);
					aaa=aaa-Math.abs(6-i)-Math.abs(6-j);
					
					if (values2[i][j] < aaa) { 
						values2[i][j] += aaa; 
					}
					values2[i][j] += check_run_num(cell,mycolor,i,j,1)*5;
					if(check_run_num(cell,mycolor,i,j,1)>0){
						
					}
				}
				
		
			}
		}
		//-------------------評価値の表示-----------------------
		int max_num=0;
		int max_i=0,max_j=0;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (max_num < values2[i][j]) {
					max_i=i;
					max_j=j;
					max_num = values2[i][j];
				}
			}
		}
		diside_i=max_i;
		diside_j=max_j;
		return(values1[max_i][max_j]);
		
	}
	
	
	
//------------------------------------------------------------------------------------------------
//自分の一手先の評価値
//-----------------------------------------------------------------------------------------------

	public int cloc_next_value(int cell [][],int mycolor,int mystone,int enemystone){
		
		int values2[][];
		values2 = new int [size][size];
		init_values2(cell,values2);
		
		int values1[][];
		values1 = new int [size][size];
		init_values2(cell,values1);
		
		//--  各マスの評価値
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				//フラグ類
				int catched_flag=0;
				int bigren_flag=0;
				int haiboku=0;
				
				// 埋まっているマスはスルー
				if (values2[i][j] == -2) { continue; }
				
		// 三々の禁じ手は打たない → -1  sub=1とする
				if(check_run_sp(cell,mycolor,i,j,1)){
					values2[i][j]=-1;
					continue;
				}
				
		//石がとれる場合と守る場合に補正をかける
				values2[i][j]=10;
				if ( check_run_sp(cell, mycolor, i, j, 12) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor, i, j, 41) ) { values2[i][j] += 12; }
		
		//石に寄せて置く形になるときは補正をかける
				if(check_run_sp(cell,mycolor,i,j,15)){values2[i][j] += 10; }
				
		//そこに置かないと相手の三連および四連ができる場合は補正をかけて防ぐ
				if ( check_run_sp(cell, mycolor*(-1), i, j, 43) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 44) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 7) ) { values2[i][j] += 20;}
				if ( check_run_sp(cell, mycolor*(-1), i, j, 17) ) { values2[i][j] += 20; }
				if ( check_run_sp(cell, mycolor*(-1), i, j, 3) ) { values2[i][j] += 20;}
				
		//そこに置くと三連および四連ができる場合は補正をかける
				if ( check_run_sp(cell,mycolor,i,j,17) ){ values2[i][j] += 20; }
				if(check_run_sp(cell,mycolor,i,j,20)){ values2[i][j] += 20; }
		
		//完全2連ができる場合は補正をかける
				if(check_run_sp(cell,mycolor,i,j,23)){ values2[i][j] += 6; }
				
		//なんか周りに石が多い時は補正をかける
				values2[i][j] += (check_run_num(cell,mycolor,i,j,2) /10);
				
		//置いたときに石がとられる形になる場合は補正をかける 行動によっては大きな補正をかけて行動を変更する
				if(check_run_sp(cell,mycolor,i,j,70)){ values2[i][j] -= 50; catched_flag=1; }
		//相手の石を置いたときに取られる形に成る場合も補正をかける
				if(check_run_sp(cell,mycolor*(-1),i,j,70)){ values2[i][j] -= 30;  }
		//四連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 45)) {haiboku =1; }
				
		//五連負け確定
		if ( check_run_sp(cell, mycolor*(-1), i, j, 46)) { haiboku =2;}
		
		// 勝利(五取) → 1000;
				if(mystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 12)) { 
						values2[i][j] += 9000;
						values1[i][j]=5000;
						continue;
					}
					
				}
				if(mystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 13)) { 
						values2[i][j] += 9000;
						values1[i][j]=5000;
						continue;
					}
					
				}
				
		// 相手の五連を崩す → 950;
				if ( check_run_sp(cell, mycolor, i, j, 30) ) {
					values2[i][j] += 8500;
					values1[i][j]=600;
					continue;
				}
				
				
		// 敗北阻止(五取) → 925;
				if(enemystone==8){
					if ( check_run_sp(cell, mycolor, i, j, 41) ) { 
						values2[i][j] += 8200;
						values1[i][j]=-500;
						continue;
					}
					
				}
				if(enemystone==6){
					if ( check_run_sp(cell, mycolor, i, j, 42) ) { 
						values2[i][j] += 8200;
						values1[i][j]=-500;
						continue;
					}
					
				}
				
		//五連の作成 900
				//五連
				if ( check_run_sp(cell, mycolor, i, j, 2) ) {
					values2[i][j] += 8100;
					values1[i][j]=900;
					continue;
				}
		//完全四連を石を取って止める
				if ( check_run_sp(cell, mycolor, i, j, 38) ) {
					values2[i][j] += 8000;
					values1[i][j]=500;
					continue;
				}
				
		//四連を石を取って止める → 775 sub=11
				
				if ( check_run_sp(cell, mycolor, i, j, 39) ) {
					values2[i][j] += 7800;
					values1[i][j]=600;
					continue;
				}
				if ( check_run_sp(cell, mycolor, i, j, 40) ) {
					values2[i][j] += 7800;
					values1[i][j]=600;
					continue;
				}
				
		//単純な四連を止める → 750  sub=8
				if ( check_run_sp(cell, mycolor*(-1), i, j, 31) ) {
					values2[i][j] += 7500;
					values1[i][j]=-200;
					continue;
				}
		
		//四四の作成 → 675 sub=3
				if(check_run_sp(cell,mycolor,i,j,4)){
					values2[i][j]+=703;
					values1[i][j]=3;
					continue;
				}
		//完全な自分の四連を作る →650  sub=7
				if(check_run_sp(cell,mycolor,i,j,3)){
					values2[i][j]+=6700;
					values1[i][j]=702;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values2[i][j]+=6500;
					values1[i][j]=701;
					continue;
				}
		//復活四連
				if(check_run_sp(cell,mycolor,i,j,18)){
					values2[i][j]+=6400;
					values1[i][j]=701;
					continue;
				}
		
		//四三を作成する
				if(check_run_sp(cell,mycolor,i,j,5)){
					values2[i][j]+=6300;
					values1[i][j]=700;
					continue;
				}
				
		//四三を防御する
				if(check_run_sp(cell,mycolor*(-1),i,j,34)){
					values2[i][j]+=5500;
					values1[i][j]=-300;
					continue;
				}
	
		
		//相手の完全な3連を石を取って崩す
				if ( check_run_sp(cell,mycolor,i,j,36) ) {
					values2[i][j] += 5800;
					values1[i][j]=500;
					continue;
				}
		
		//完全な相手の3連を防ぐ → 550 sub=5
				if(check_run_sp(cell,mycolor*-1,i,j,32)){
					values2[i][j]+=5600;
					values1[i][j]=-200;
					continue;
				}
		//復活三連
				if(check_run_sp(cell,mycolor,i,j,19)){
					values2[i][j]+=5400;
					values1[i][j]=400;
					continue;
				}
		//飛び三三を作る → 530 sub=15
				if(check_run_sp(cell,mycolor,i,j,6)){
					values2[i][j]+=5300;
					values1[i][j]=400;
				}
				
		//石とり複数
				if(check_run_sp(cell, mycolor, i, j, 13)){
					values2[i][j]+=5200;
					values1[i][j]=500;
					continue;
				}
		//とった石が６つ以上なら石をとるのを優先 →450
				if(mystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,12) ) { 
						values2[i][j] += 5300;
						values1[i][j]=300;
						continue;
					}
					
				}
		//相手の三三を防ぐ
				if(check_run_sp(cell,mycolor*(-1),i,j,35)){
					values2[i][j]+=5000;
					values1[i][j]=-400;
					continue;
				}
		
		//とられた石が６つ以上なら石を守るのを優先
				if(enemystone>=6){
					if ( check_run_sp(cell,mycolor,i,j,41) ) { 
						values2[i][j] += 4800;
						values1[i][j]=-100;
						continue;
					}
					
				}
		// 相手の石を取る → 420;
				if ( check_run_sp(cell,mycolor,i,j,12) ) { 
					values2[i][j] += 4900;
					values1[i][j]=400;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku >= 1){
						values2[i][j] = 7700;
					}
					continue;
				}
		//石ガード複数
				if(check_run_sp(cell, mycolor, i, j, 42)){
					
					values2[i][j]+=4700;
					if(catched_flag == 1){
						values2[i][j] =100;
					}
					continue;
				}
		
		//自分の完全な三連を作る
				if(check_run_sp(cell,mycolor,i,j,20)){
					values2[i][j]+=4650;
					values1[i][j]=300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		
	// 自分の四連を作る補正つき
				if ( check_run_sp(cell,mycolor,i,j,21) ) {
					values2[i][j] += 4630;
					values1[i][j]=300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		// 自分の四連を作る → 600;
				if ( check_run_sp(cell,mycolor,i,j,22) ) {
					values2[i][j] += 4590;
					values1[i][j]=300;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					
					continue;
				}
		
				
		// 自分の石を守る → 400;
				if ( check_run_sp(cell,mycolor,i,j,41) ) { 
					values2[i][j] += 4400;
					values1[i][j]=-100;
					if(catched_flag == 1){
						values2[i][j]=0;
					}
					continue;
				}
		
				
		//石とり準備複数
				if(check_run_sp(cell,mycolor,i,j,16)){
					values2[i][j]+=4000;
					values1[i][j]=200;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku == 1){
						values2[i][j] = 7600;
					}
					continue;
				}
				
				
		//石とり準備
				if(check_run_sp(cell,mycolor,i,j,15)){
					values2[i][j]+=3500;
					values1[i][j]=100;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					if(haiboku == 1){
						values2[i][j] = 7500;
					}
					continue;
				}
		
		
		//自分の完全な飛び三連を作る
				if(check_run_sp(cell,mycolor,i,j,9)){
					
					values2[i][j]+=3000;
					values1[i][j]=200;
					if(catched_flag == 1){
						values2[i][j] =0;
					}
					continue;
				}
		
				
		// 相手の三連を防ぐ → 500;
				if ( check_run_sp(cell,mycolor,i,j,43) ) {
					values2[i][j] += 2000;
					values1[i][j]=0;
					continue;
				}
				
		// ランダム
				if (values2[i][j] <= 10) {
					int aaa = (int) Math.round(Math.random() * 5 +20);
					aaa=aaa-Math.abs(6-i)-Math.abs(6-j);
					
					if (values2[i][j] < aaa) { 
						values2[i][j] += aaa; 
					}
					values2[i][j] += check_run_num(cell,mycolor,i,j,1)*5;
					if(check_run_num(cell,mycolor,i,j,1)>0){
						
					}
				}
				
		
			}
		}
		//-------------------評価値の表示-----------------------
		
		
		//-------------------評価値の表示-----------------------
		int max_num=0;
		int max_i=0,max_j=0;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (max_num < values2[i][j]) {
					max_i=i;
					max_j=j;
					max_num = values2[i][j];
				}
			}
		}
		
		return(values1[max_i][max_j]);
		
		
	}
	
	
//-----------------------------------------------------------------------------
//数を数える必要がある連の全周チェック  ぶっちゃけこっちだけでいい
//------------------------------------------------------------------------------
	int check_run_num(int[][] board, int color, int i, int j,int sub){
		int ans_num =0;		//返却する値
		
		//指定されたマスの周りをループさせて調べる
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				//-------序盤所手をいい位置におく----------
				if(sub == 1){
					ans_num+=single_zyoban(board, color, i, j, dx, dy,1);
				}
				if(sub == 2){
					ans_num+=single_zyoban2(board, color, i, j, dx, dy,1);
				}
			}
		}
		return(ans_num);
	}
	
	
//-------------------------------------------------------------------
//場合に応じて検定する連の全周チェック
//--------------------------------------------------------------------
	boolean check_run_sp(int[][] board, int color, int i, int j,int sub) {
		int count_a=0;
		int count_b=0;
		int count_c=0;
		int count_d=0;
		int count_e=0;
		int dx2=0,dy2=0;
		
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				
				//三三禁止
				if(sub==1){
					if ( p_a_three1(board, color, i, j, dx, dy,3) ) { count_a++; }
					if(p_a_three2(board, color, i, j, dx, dy,3) ){ count_b++; }
					
					if(count_a >=1 && count_b >= 1){ return true; }
					if(count_a >=2){ return true; }
					if(count_b >=3){ return true; }
				}
				
		//-------------------------------攻撃----------------------------------------------------
				//五連作成
				if(sub==2){
					if ( p_d_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if ( p_d_four2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( p_d_four3(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//完全四連作成
				if(sub==3){
					if ( p_a_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if ( p_a_four2(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//四四作成
				if(sub==4){
					if (p_a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; continue;}}
					if ( p_a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; continue;}}
					if ( a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four4(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four4(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					
					if(count_a >=2){return true;}
				}
				//四三作成
				if(sub==5){
					if (p_a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( p_a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four4(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four4(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++;}}
					
					
					if(count_a >= 1 && count_b >= 1){return true;}
				}
				
				//三三を作成
				if(sub==6){
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_f_a_three1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					
					if(count_a >= 1 && count_b >= 1){return true;}
					if(count_b >= 2){return true;}
				}
				
				//片側四連作成
				if(sub==7){
					if ( a_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four3(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four4(board, color, i, j, dx, dy,5) ) { return true; }
					
				}
				if(sub==17){
					if ( f_a_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four3(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four4(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//補正あり片側四連作成
				if(sub==21){
					if ( a_four1_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four2_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four3_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( a_four4_2(board, color, i, j, dx, dy,5) ) { return true; }
					
				}
				if(sub==22){
					if ( f_a_four1_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four2_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four3_2(board, color, i, j, dx, dy,5) ) { return true; }
					if ( f_a_four4_2(board, color, i, j, dx, dy,5) ) { return true; }
				}
				//完全三連作成
				if(sub==8){
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { return true; }
					
				}
				//完全三連作成
				if(sub==20){
					if (p_a_three2_2(board, color, i, j, dx, dy,2) ) { return true; }
					if (p_a_three1_2(board, color, i, j, dx, dy,2) ) { return true; }
					
				}
				//完全飛び三連作成
				if(sub==9){
					if (p_f_a_three1(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//片側三連作成
				if(sub==10){
					if (a_three1(board, color, i, j, dx, dy,5) ) { return true; }
					if (a_three2(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//片側飛び三連
				if(sub==11){
					if (f_a_three1(board, color, i, j, dx, dy,5) ) { return true; }
					if (f_a_three2(board, color, i, j, dx, dy,5) ) { return true; }
					if (f_a_three3(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//石取り
				if(sub==12){
					if (get_two(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//石取り複数
				if(sub==13){
					if (get_two(board, color, i, j, dx, dy,5) ) { count_a++; }
				}
				
				
				//石取り準備
				if(sub==15){
					if (p_two_len(board, color, i, j, dx, dy,5) ) { return true; }
				}
				//石取り準備複数
				if(sub==16){
					if (p_two_len(board, color, i, j, dx, dy,5) ) { count_a++; }
					if(count_a >= 2){return true;}
				}
				
				//復活四連
				if(sub==18){
					if(seach_crush(board, color, i, j, dx, dy,70) ) { return true; }
				}
				
				//復活三連
				if(sub==19){
					if(seach_crush(board, color, i, j, dx, dy,60) ) { return true; }
				}
				
				//完全二連の作成
				if(sub==23){
					if(p_a_two(board, color, i, j, dx, dy,5)) {return true; }
				}
				//組み合わせ
				
				
				
		//--------------------------------防御----------------------------------------------------
				//石取り五連阻止
				if(sub==30){
					if(seach_crush(board, color, i, j, dx, dy,50) ) { return true; }
				}
				
				//片側四連阻止
				if(sub==31){
					if (p_d_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_d_four2(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_d_four3(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//三連阻止
				if(sub==32){
					if (p_d_three1(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_d_three2(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//四四阻止
				if(sub==33){
					if (p_a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; continue;}}
					if ( p_a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; continue;}}
					if ( a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four4(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four4(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					
					if(count_a >=2){return true;}
				}
				
				//四三阻止
				if(sub==34){	
					if (p_a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( p_a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( a_four4(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if ( f_a_four4(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three1(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { if(((dx2*(-1)) != dx || (dy2*(-1)) != dy ) && (dx2 != dx || dy2 != dy)){ dx2 = dx ; dy2 = dy; count_b++;}}
					
					if(count_a >= 1 && count_b >= 1){return true;}
				}
				
				//三三阻止
				if(sub==35){
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_a++; }}
					if (p_f_a_three1(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { if((dx2*(-1)) != dx || (dy2*(-1)) != dy ){ dx2 = dx ; dy2 = dy; count_b++; }}
					
					if(count_a >= 1 && count_b >= 1){return true;}
					if(count_b >= 2){return true;}
				}
				
				//石取り三連阻止
				if(sub==36){
					if(seach_crush(board, color, i, j, dx, dy,30) ) { return true; }
				}
				
				//石取り飛び三連阻止
				if(sub==37){
					if(seach_crush(board, color, i, j, dx, dy,31) ) { return true; }
				}
				
				//石取り完全四連阻止
				if(sub==38){
					if(seach_crush(board, color, i, j, dx, dy,40) ) { return true; }
				}
				
				//石取り片側四連阻止
				if(sub==39){
					if(seach_crush(board, color, i, j, dx, dy,41) ) { return true; }
				}
				
				//石取り飛び四連阻止
				if(sub==40){
					if(seach_crush(board, color, i, j, dx, dy,42) ) { return true; }
				}
				
				//石取り阻止
				if(sub==41){
					if (gard_two(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				//石取り阻止複数
				if(sub==42){
					if (gard_two(board, color, i, j, dx, dy,5) ) {  count_a++; }
					if(count_a >= 2){return true;}
				}
				
				//片側三連阻止
				if(sub==43){
					if (a_four1(board, color, i, j, dx, dy,5) ) { return true; }
					if (a_four2(board, color, i, j, dx, dy,5) ) { return true; }
					if (a_four3(board, color, i, j, dx, dy,5) ) { return true; }
					
				}
				
				//完全三連作成阻止
				if(sub==44){
					if (p_a_three1(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_a_three2(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_f_a_three2(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_f_a_three3(board, color, i, j, dx, dy,5) ) { return true; }
					
					
				}
				
				//相手の四連完成
				if(sub == 45){
					if (p_d_four1(board, color, i, j, dx, dy,5) ) { return true; }
				}
				//相手の五連完成
				if(sub ==46){
					if (p_d_five1(board, color, i, j, dx, dy,5) ) { return true; }
				}
				//飛び三連横叩き
				if(sub ==47){
					if (p_d_three3(board, color, i, j, dx, dy,5) ) { return true; }
					if (p_d_three4(board, color, i, j, dx, dy,5) ) { return true; }
				}
				
				
				//--------------------------------乱数-----------------------------------------------------
				//乱数での石置き
				if(sub==60){
					if(ransu(board, color, i, j, dx, dy,3)){return true; }
				}
				
				//--------------------------------その他--------------------------------------------------
				//駒を置いた場合にとられる
				if(sub==70){
					if (cached_koma1(board, color, i, j, dx, dy,5) ) { return true; }
					if (cached_koma2(board, color, i, j, dx, dy,5) ) { return true; }
					
				}
				
				//負け確定石とり準備
				
				
				
			}
			
		}
		
		
		return false;
	}
	

//---------------------------------------------------------------
//乱数の値をよい位置に置く
//---------------------------------------------------------------
	boolean ransu(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k = 1;
		int count;
		if ( i+dx < 0 || j+dy < 0 || i+dx >= size || j+dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*-1 ) { return false; }
		
		k=-1;
		
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		
		return(true);
	}
//----------------------------------------------------------------------------------------
//通常指す際に指し手の周辺に自分の色がない、かつ一つ飛びで自分の色がある
//-------------------------------------------------------------------------------------
	int single_zyoban(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k =1;
		int count =0;			//自分の石の数を数える
		
		if ( i+dx < 0 || j+dy < 0 || i+dx >= size || j+dy >= size ) { return 0; }
		if ( board[i+k*dx][j+k*dy] == color ) { return 0; }
		if ( board[i+k*dx][j+k*dy] == color*(-1)) { return 6; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return 0; }
		if ( board[i+k*dx][j+k*dy] == color) { return 9; }
		if ( board[i+k*dx][j+k*dy] == color*(-1)) { return 3; }
		
		return(0);
	}

//-----------------------通常用乱数-------------------
	int single_zyoban2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k =1;
		int count =0;			//自分の石の数を数える
		
		if ( i+dx < 0 || j+dy < 0 || i+dx >= size || j+dy >= size ) { return 0; }
		if ( board[i+k*dx][j+k*dy] == color*(-1)) { return 6; }
		if ( board[i+k*dx][j+k*dy] == color) { return 3; }
		
		
		
		return(0);
	}
//-------------------------------------------------------------------------
//置いた場所の駒が取られるパターンの検出
//-------------------------------------------------------------------------
//-------------------　△○●---------------------------------
	boolean cached_koma1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		return(true);
	}
//--------------------　○△●--------------------------------------------
	boolean cached_koma2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		return(true);
	}
	

	
//----------------------------------------------------------------------------------
//5連関連
//-------------------------------------------------------------------------------------
	boolean p_d_five1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=5;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=6;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		
		return true;
	}


//----------------------------------------------------------------------------------
//4連関連
//-------------------------------------------------------------------------------------

//----------------------------------発見行動----------------------------	
	
//----------○○○○通常４連-------------
	boolean p_d_four1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=5;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size) { 
			
		}else{
			if ( board[i+k*dx][j+k*dy] == color ){ return false; }
		}
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size) { 
			
		}else{
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}

		return true;
	}
//-----------○　○○○　四連-------
	boolean p_d_four2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-2;
		if ( i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { 
		}else{
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size) {
		}else{
			
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}
//---------------○○　○○ 四連---------------
	boolean p_d_four3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { 
		}else{
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) {
		}else{
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}


//-----------------------------------攻撃行動------------------------------
		
//-------------△○○○ 通常完全4連の作成--------------
	boolean p_a_four1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}

//--------------○△○○ 通常完全四連の作成------------------
	boolean p_a_four2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return true;
	}

//----------------△○○○● 補正あり片側4連の作成-------------------------
	boolean a_four1_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
		
	}
		
		
//------------------○△○○●　補正あり片側4連の作成-------------------------------
	boolean a_four2_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		
		
//---------------------○○△○● 補正あり片側4連の作成-------------------------------
	boolean a_four3_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-4;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}


//---------------------○○○△● 補正あり片側4連の作成-------------------------------
	boolean a_four4_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}


//--------------△　○○○ 補正あり飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four1_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=-1;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		
//--------------○　△○○ 補正あり飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four2_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=-3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

//--------------○　○△○ 補正あり飛び完全4連の作成(片側と同じ扱い)----------------
	boolean f_a_four3_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-4;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=2;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

//--------------○　○○△ 補正あり飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four4_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,2) ) { return false; }
		k=-5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=1;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		

	
//----------------△○○○● 片側4連の作成-------------------------
	boolean a_four1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
		
	}
		
		
//------------------○△○○● 片側4連の作成-------------------------------
	boolean a_four2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		
		
//---------------------○○△○● 片側4連の作成-------------------------------
	boolean a_four3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-4;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}


//---------------------○○○△● 片側4連の作成-------------------------------
	boolean a_four4(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}


//--------------△　○○○ 飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-1;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		
//--------------○　△○○ 飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=-3;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

//--------------○　○△○ 飛び完全4連の作成(片側と同じ扱い)----------------
	boolean f_a_four3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-4;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=2;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

//--------------○　○○△ 飛び完全4連の作成(片側と同じ扱い)-----------------
	boolean f_a_four4(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }k=-4;
		k=-5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=1;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}

		
		
		
//--------------△○　○○ 飛び完全4連の作成(片側と同じ扱い、作らないほうがいい)-----------------
	boolean f_a_four5(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-1;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=5;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}
//--------------○△　○○ 飛び完全4連の作成(片側と同じ扱い、作らないほうがいい)-----------------
	boolean f_a_four6(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		k=4;
		if (  i+k*dx>= 0 && j+k*dy >= 0 && i+k*dx < size && j+k*dy < size ) { 
			if ( board[i+k*dx][j+k*dy] == color ) { return false; }
		}
		return true;
	}
//--------------------------------------------------------------------------------
//三連関連
//------------------------------------------------------------------------------------

//---------------------------------発見行動---------------------------------------
		
		
//-------------------　○○○　 通常完全三連----------------------
	boolean p_d_three1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}


		
//-------------------○△○○　 飛び完全三連----------------------
	boolean p_d_three2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}
	
//-------------------△○　○○　 飛び完全三連横叩きガード----------------------
	boolean p_d_three3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=5;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}

	
//-------------------△○○　○　 飛び完全三連横叩きガード----------------------
	boolean p_d_three4(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=5;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}
		
		
//-------------------------------------攻撃行動-----------------------------

//----------------　△○○　　完全三連---------------------------
	boolean p_a_three1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return true;
		
	}




//----------------　○△○　完全三連---------------------------
	boolean p_a_three2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return true;
	}

	
	
	
//----------------　△○○　　完全三連補正あり---------------------------
	boolean p_a_three1_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,len) ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,len) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		
		return true;
		
	}

//----------------　○△○　完全三連補正あり---------------------------
	boolean p_a_three2_2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,len) ) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		if (hosei(board, color, i, j, i+k*dx, j+k*dy,len) ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return true;
	}

	

//----------------△　○○　飛び三連---------------------------
	boolean p_f_a_three1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}

//----------------○　△○　飛び三連---------------------------
	boolean p_f_a_three2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}
	

//----------------○　○△　飛び三連---------------------------
	boolean p_f_a_three3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		return true;
	}


//----------------------------片側三連関連(先読みに利用)-------------------------

//----------------　△○○　　片側三連---------------------------
	boolean a_three1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		return true;
	}




//----------------　○△○　片側三連---------------------------
	boolean a_three2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		return true;
	}
	

//----------------△　○○　片側飛び三連---------------------------
	boolean f_a_three1(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		
		return true;
	}

//----------------○　△○　片側飛び三連---------------------------
	boolean f_a_three2(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		
		return true;
	}
	

//----------------○　○△　片側飛び三連---------------------------
	boolean f_a_three3(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=-2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0) { return false; }
		
		k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		k=-3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=-4;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		
		return true;
	}
//-----------------△○　完全二連作成-------------------------
	boolean p_a_two(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0) { return false; }
		
		k=-1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return true;
	}


//----------------------------------------------------------------------------------
//石取り関連
//-----------------------------------------------------------------------------------------------
	
//-----------------------△●●○　石の取れるところを探す--------------------------------------
	boolean get_two(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color *(-1) ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color *(-1) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		return true;
	
	}

//------------------------△○○●　石が取られるところを探す------------------------------------
	boolean gard_two(int[][] board, int color, int i, int j, int dx, int dy,int len){
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color *(-1)) { return false; }
		
		return true;
	
	}
	
//-------------------------●●△　石を取りに行く準備--------------------------------------------
	boolean p_two_len(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != 0 ) { return false; }
		
		return(true);
	}

	

//---------------------------------------------------------------
//連崩しの検索
//---------------------------------------------------------------
	boolean seach_crush(int[][] board, int color, int i, int j, int dx, int dy, int enemy) {
		int len = 3;
		int x2[],y2[];
		
		x2 = new int[2];
		y2 = new int[2];
		
		int k=1;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		k=2;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color*(-1) ) { return false; }
		
		k=3;
		if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { return false; }
		if ( board[i+k*dx][j+k*dy] != color) { return false; }
		
		
		x2[0]=i+1*dx;
		y2[0]=j+1*dy;
		x2[1]=i+2*dx;
		y2[1]=j+2*dy;
		
		
		
		color*=-1;
		for(k=0;k<2;k++){
			for ( int ax = -1; ax <= 1; ax++ ) {
				for ( int ay = -1; ay <= 1; ay++ ) {
					if ( ax == 0 && ay == 0 ) { continue; }
					//完全五連を崩す
					if(enemy == 50){
						if ( p_d_four1(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( p_d_four2(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( p_d_four3(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
					}
					//完全四連を崩す
					if(enemy ==40){
						if ( p_a_four1(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( p_a_four2(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
					}
					if(enemy ==41){									
						//片側四連崩す
						if ( a_four1(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( a_four2(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( a_four3(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						
					}
					//飛び四連崩し　状況が変わらない
					if(enemy == 42){
						if ( f_a_four2(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if ( f_a_four3(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
					}
					if(enemy ==30){
						//完全3崩す

						
						if (p_a_three1(board, color, x2[k], y2[k], ax, ay,5) ) {  return true; }
						if (p_a_three2(board, color, x2[k], y2[k], ax, ay,5) ) {  return true; }
						
						//飛び三連崩して状況そのまま
						if (p_f_a_three2(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if (p_f_a_three1(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if (p_f_a_three3(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
					}
					//飛び三連崩して状況が変わる
					if(enemy ==31){
						if (p_f_a_three1(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
						if (p_f_a_three3(board, color, x2[k], y2[k], ax, ay,5) ) { return true; }
					}
				}
			}
			
		}
		color*=-1;
		for(k=0;k<2;k++){
			//四連復活
			if(enemy == 70){
				if(check_run_sp(board,color,x2[k],y2[k],31)){return true;}
				if(check_run_sp(board,color,x2[k],y2[k],45)){return true;}
			}
			//完全三連復活
			if(enemy == 60){
				if(check_run_sp(board,color,x2[k],y2[k],32)){return true;}
			}
		}
		return false;
	}

	
//---------------------------文字の出力------------------
	void mozi(){
		
	}

//----------------------------------------------------------------------
//石を取られないおよびガード時に三連、４連が作成されない
//----------------------------------------------------------------------------
	boolean hosei(int[][] board, int color, int i, int j, int dx, int dy,int len){
		
		//石が取られるかどうか
		if(len == 2){
			if(check_run_sp(board,color,dx,dy,70)){ return true;}
		}
	
		//完全三連ができるかどうか
		if(len ==3){
			if(check_run_sp(board,color,dx,dy,8)){ return true;}
			if(check_run_sp(board,color,dx,dy,9)){ return true;}
		}
	
		//四連を作られるかどうか
		if(len ==4 ){
			if(check_run_sp(board,color,dx,dy,7)){ return true;}
			if(check_run_sp(board,color,dx,dy,17)){ return true;}
		}
	
		//誘導した先で相手の石を取れるかどうか
		if(len == 5){
			if(check_run_sp(board,color,dx,dy,70)){ return true;}
		}
		return false;
	
	}

//---------------------------------------------------------------
//飛び五連の検索
//---------------------------------------------------------------
	boolean tobi(int[][] board, int color, int i, int j, int dx, int dy, int len) {
		int sub=0;
		int k,m;
		for ( k = 1; k < len; k++ ) {
			int x = i+k*dx;
			int y = j+k*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+k*dx][j+k*dy] != color ) {
				break;
			}	
		}
		sub=k;
		
		for ( m = -1; k < len+1; k++,m-- ) {
			int x = i+m*dx;
			int y = j+m*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+m*dx][j+m*dy] != color ) { return false;}	
		}
		if(i+sub*dx <0 || j+sub*dy <0 || i+m*dx<0 ||j+m*dy<0|| i+sub*dx >=size || j+sub*dy >=size || i+m*dx>=size ||j+m*dy >=size){
			return false;
		}
		return true;
	}

//-------------------------------------------------------------------
//三三を発見(二連の発見にも活用)
//---------------------------------------------------------------
	boolean sazan(int[][] board, int color, int i, int j, int dx, int dy,int len){
		for ( int k = 1; k < len; k++ ) {
			int x = i+k*dx;
			int y = j+k*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+k*dx][j+k*dy] != color ) { return false; }
		}
		return true;
	}
	
//----------------------------------------------------------------
//  連の全周チェック
//----------------------------------------------------------------

	boolean check_run(int[][] board, int color, int i, int j, int len) {
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				if ( check_run_dir(board, color, i, j, dx, dy, len) ) { return true; }
			}
		}
		return false;
	}

//----------------------------------------------------------------
//  連の方向チェック(止連・端連・長連も含む、飛びは無視)
//----------------------------------------------------------------
  
	boolean check_run_dir(int[][] board, int color, int i, int j, int dx, int dy, int len) {
		int sub=0;
		int k,m;
		for ( k = 1; k < len; k++ ) {
			int x = i+k*dx;
			int y = j+k*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+k*dx][j+k*dy] != color ) {
				
				break;
			}	
			
		}
		sub=k;
		
		for ( m = -1; k < len+1; k++,m-- ) {
			int x = i+m*dx;
			int y = j+m*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+m*dx][j+m*dy] != color ) { return false;}
			
			
		}
		if(i+sub*dx <0 || j+sub*dy <0 || i+m*dx<0 ||j+m*dy<0|| i+sub*dx >=size || j+sub*dy >=size || i+m*dx>=size ||j+m*dy >=size){
			return false;
		}
		
		if ( board[i+sub*dx][j+sub*dy] == (-1)*color && board[i+m*dx][j+m*dy] == (-1)*color){ 
				
				return false; 
				
		}
		return true;
	}

//----------------------------------------------------------------
//  取の全周チェック(ダブルの判定は無し)
//----------------------------------------------------------------
  
	boolean check_rem(int [][] board, int color, int i, int j) {
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				if ( check_rem_dir(board, color, i, j, dx, dy) ) { return true; }
			}
		}
		return false;
	}

//----------------------------------------------------------------
//  取の方向チェック
//----------------------------------------------------------------
  
	boolean check_rem_dir(int[][] board, int color, int i, int j, int dx, int dy) {
		int len = 3;
		for ( int k = 1; k <= len; k++ ) {
			int x = i+k*dx;
			int y = j+k*dy;
			if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }
			if ( board[i+k*dx][j+k*dy] != color ) { return false; }
			if (k == len-1) { color *= -1; }
		}
		return true;
	}
//----------------------------------------------------------------
//  着手の決定
//----------------------------------------------------------------

	public GameHand deside_hand() {
		GogoHand hand = new GogoHand();
		hand.set_hand(0, 0);  // 左上をデフォルトのマスとする
		int value = -1;       // 評価値のデフォルト
	//--  評価値が最大となるマス
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (value < values[i][j]) {
					hand.set_hand(i, j);
					value = values[i][j];
				}
			}
		}
		
		
		return hand;  	
	}

	
//-----------------------------------------------------------------------
//次の局面の生成を行う
//----------------------------------------------------------------------------------
//---------------cellに仮の指し手を指す------------------------------
	int deside_hand2(int cell [][],int sub_value[][],int mycolor) {
		int value = -1;       // 評価値のデフォルト
		int max_i=0,max_j=0;
		int get_num =0;
	//--  評価値が最大となるマス
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (value < sub_value[i][j]) {
					max_i=i;
					max_j=j;
					value = sub_value[i][j];
				}
			}
		}
		
		int i=max_i;
		int j=max_j;
		
		//--------------駒が消えるかどうかの判定------
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				
				int k=1;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor*(-1) ) {continue; }
				k=2;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor*(-1) ) {continue; }
				 k=3;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor ) {continue; }
				
				k=1;
				cell[i+k*dx][j+k*dy] =0;
				k=2;
				cell[i+k*dx][j+k*dy] =0;
				get_num++;
		
			}
		}
		cell[max_i][max_j]=mycolor;
		
		return(get_num);
		
	}
	
//---------------cellに仮の指し手を指す------------------------------
	int deside_hand3(int cell [][],int sub_value[][],int mycolor,int i,int j) {
		int value = -1;       // 評価値のデフォルト
		int max_i=0,max_j=0;
		int get_num =0;
		
		//--------------駒が消えるかどうかの判定------
		for ( int dx = -1; dx <= 1; dx++ ) {
			for ( int dy = -1; dy <= 1; dy++ ) {
				if ( dx == 0 && dy == 0 ) { continue; }
				
				int k=1;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor*(-1) ) {continue; }
				k=2;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor*(-1) ) {continue; }
				 k=3;
				if (  i+k*dx< 0 || j+k*dy < 0 || i+k*dx >= size || j+k*dy >= size ) { continue; }
				if ( cell[i+k*dx][j+k*dy] != mycolor ) {continue; }
				
				k=1;
				cell[i+k*dx][j+k*dy] =0;
				k=2;
				cell[i+k*dx][j+k*dy] =0;
				get_num++;
		
			}
		}
		cell[i][j]=mycolor;
		
		return(get_num);
		
	}
	
	
	/*
	public v_num(){
		int val[];
		val = new int [1000];
		
		
//---------------------------攻撃------------------
		val[0]=9000;		//五取り
		val[1]=9000;		//五取り複数
		val[2]=8500;		//相手の五連を崩す
		val[3]=8200;		//敗北阻止(五取)
		val[4]=8100;		//五連の作成
		val[5]=8000;		//完全四連を石を取って止める
		val[6]=7800;		//
		val[7]=7500;
		val[8]=6800;
		val[9]=6700;
		val[10]=6300;
		val[11]=6500;
		val[12]=5800;
		val[13]=5600;
		val[14]=5400;
		val[15]=5300;
		val[16]=5200;
		val[17]=5300;
		val[18]=4800;
		val[19]=;
		val[20]=;
		val[21]=;
		val[22]=;
		val[23]=;
		val[24]=;
		val[25]=;
		val[26]=;
		val[27]=;
		val[28]=;
		val[29]=;
		
//----------------------------防御----------------------
		val[100]=;
		val[101]=;
		val[102]=;
		val[103]=;
		val[104]=;
		val[105]=;
		val[106]=;
		val[107]=;
		val[108]=;
		val[109]=;
		val[110]=;
		val[111]=;
		val[112]=;
		val[113]=;
		val[114]=;
		val[115]=;
		val[116]=;
		val[117]=;
		val[118]=;
		val[119]=;
		val[120]=;
		val[121]=;
		
		
		
		
		
	}
	*/
	
	
	
	
	
	
	
	
	
	
}