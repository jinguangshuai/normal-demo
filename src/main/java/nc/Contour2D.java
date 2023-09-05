package nc;

import wcontour.Contour;
import wcontour.global.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @Author: yzmiao
* @Description: 修改等值面算法，使其支持二维坐标
* @Date: 2021年5月20日 下午2:11:52
* @Version: V1.0
**/
public class Contour2D extends Contour {
	
	private static List<EndPoint> _endPointList = new ArrayList<EndPoint>();
	/**
	 * Tracing contour borders of the grid data with undefined data. Grid data
	 * are from left to right and from bottom to top. Grid data array: first
	 * dimention is Y, second dimention is X.
	 *
	 * @param S0 input grid data
	 * @param X x coordinate array
	 * @param Y y coordinate array
	 * @param S1 data flag array
	 * @param undefData undefine data
	 * @return borderline list
	 */
	public static List<Border> tracingBorders(double[][] S0, double[][] X, double[][] Y, int[][] S1, double undefData) {
		List<BorderLine> borderLines = new ArrayList<>();
		
		int m, n, i, j;
		m = S0.length;    //Y
		n = S0[0].length;    //X
		
		//S1 = new int[m][n];    //---- New array (0 with undefine data, 1 with data)
		for (i = 0; i < m; i++) {
			for (j = 0; j < n; j++) {
				if (doubleEquals(S0[i][j], undefData)) //Undefine data
				{
					S1[i][j] = 0;
				} else {
					S1[i][j] = 1;
				}
			}
		}
		
		//---- Border points are 1, undefine points are 0, inside data points are 2
		//l - Left; r - Right; b - Bottom; t - Top; lb - LeftBottom; rb - RightBottom; lt - LeftTop; rt - RightTop
		int l, r, b, t, lb, rb, lt, rt;
		for (i = 1; i < m - 1; i++) {
			for (j = 1; j < n - 1; j++) {
				if (S1[i][j] == 1) //data point
				{
					l = S1[i][j - 1];
					r = S1[i][j + 1];
					b = S1[i - 1][j];
					t = S1[i + 1][j];
					lb = S1[i - 1][j - 1];
					rb = S1[i - 1][j + 1];
					lt = S1[i + 1][j - 1];
					rt = S1[i + 1][j + 1];
					
					if (l > 0 && r > 0 && b > 0 && t > 0 && lb > 0 && rb > 0 && lt > 0 && rt > 0) {
						S1[i][j] = 2;    //Inside data point
					}
					if (l + r + b + t + lb + rb + lt + rt <= 2) {
						S1[i][j] = 0;    //Data point, but not more than 3 continued data points together.
					}                        //So they can't be traced as a border (at least 4 points together).
					
				}
			}
		}
		
		//---- Remove isolated data points (up, down, left and right points are all undefine data).
		boolean isContinue;
		while (true) {
			isContinue = false;
			for (i = 1; i < m - 1; i++) {
				for (j = 1; j < n - 1; j++) {
					if (S1[i][j] == 1) //data point
					{
						l = S1[i][j - 1];
						r = S1[i][j + 1];
						b = S1[i - 1][j];
						t = S1[i + 1][j];
						lb = S1[i - 1][j - 1];
						rb = S1[i - 1][j + 1];
						lt = S1[i + 1][j - 1];
						rt = S1[i + 1][j + 1];
						if ((l == 0 && r == 0) || (b == 0 && t == 0)) //Up, down, left and right points are all undefine data
						{
							S1[i][j] = 0;
							isContinue = true;
						}
						if ((lt == 0 && r == 0 && b == 0) || (rt == 0 && l == 0 && b == 0)
								|| (lb == 0 && r == 0 && t == 0) || (rb == 0 && l == 0 && t == 0)) {
							S1[i][j] = 0;
							isContinue = true;
						}
					}
				}
			}
			if (!isContinue) //untile no more isolated data point.
			{
				break;
			}
		}
		//Deal with grid data border points
		for (j = 0; j < n; j++) //Top and bottom border points
		{
			if (S1[0][j] == 1) {
				if (S1[1][j] == 0) //up point is undefine
				{
					S1[0][j] = 0;
				} else if (j == 0) {
					if (S1[0][j + 1] == 0) {
						S1[0][j] = 0;
					}
				} else if (j == n - 1) {
					if (S1[0][n - 2] == 0) {
						S1[0][j] = 0;
					}
				} else if (S1[0][j - 1] == 0 && S1[0][j + 1] == 0) {
					S1[0][j] = 0;
				}
			}
			if (S1[m - 1][j] == 1) {
				if (S1[m - 2][j] == 0) //down point is undefine
				{
					S1[m - 1][j] = 0;
				} else if (j == 0) {
					if (S1[m - 1][j + 1] == 0) {
						S1[m - 1][j] = 0;
					}
				} else if (j == n - 1) {
					if (S1[m - 1][n - 2] == 0) {
						S1[m - 1][j] = 0;
					}
				} else if (S1[m - 1][j - 1] == 0 && S1[m - 1][j + 1] == 0) {
					S1[m - 1][j] = 0;
				}
			}
		}
		for (i = 0; i < m; i++) //Left and right border points
		{
			if (S1[i][0] == 1) {
				if (S1[i][1] == 0) //right point is undefine
				{
					S1[i][0] = 0;
				} else if (i == 0) {
					if (S1[i + 1][0] == 0) {
						S1[i][0] = 0;
					}
				} else if (i == m - 1) {
					if (S1[m - 2][0] == 0) {
						S1[i][0] = 0;
					}
				} else if (S1[i - 1][0] == 0 && S1[i + 1][0] == 0) {
					S1[i][0] = 0;
				}
			}
			if (S1[i][n - 1] == 1) {
				if (S1[i][n - 2] == 0) //left point is undefine
				{
					S1[i][n - 1] = 0;
				} else if (i == 0) {
					if (S1[i + 1][n - 1] == 0) {
						S1[i][n - 1] = 0;
					}
				} else if (i == m - 1) {
					if (S1[m - 2][n - 1] == 0) {
						S1[i][n - 1] = 0;
					}
				} else if (S1[i - 1][n - 1] == 0 && S1[i + 1][n - 1] == 0) {
					S1[i][n - 1] = 0;
				}
			}
		}
		
		//---- Generate S2 array from S1, add border to S2 with undefine data.
		int[][] S2 = new int[m + 2][n + 2];
		for (i = 0; i < m + 2; i++) {
			for (j = 0; j < n + 2; j++) {
				if (i == 0 || i == m + 1) //bottom or top border
				{
					S2[i][j] = 0;
				} else if (j == 0 || j == n + 1) //left or right border
				{
					S2[i][j] = 0;
				} else {
					S2[i][j] = S1[i - 1][j - 1];
				}
			}
		}
		
		//---- Using times number of each point during chacing process.
		int[][] UNum = new int[m + 2][n + 2];
		for (i = 0; i < m + 2; i++) {
			for (j = 0; j < n + 2; j++) {
				if (S2[i][j] == 1) {
					l = S2[i][j - 1];
					r = S2[i][j + 1];
					b = S2[i - 1][j];
					t = S2[i + 1][j];
					lb = S2[i - 1][j - 1];
					rb = S2[i - 1][j + 1];
					lt = S2[i + 1][j - 1];
					rt = S2[i + 1][j + 1];
					//---- Cross point with two boder lines, will be used twice.
					if (l == 1 && r == 1 && b == 1 && t == 1 && ((lb == 0 && rt == 0) || (rb == 0 && lt == 0))) {
						UNum[i][j] = 2;
					} else {
						UNum[i][j] = 1;
					}
				} else {
					UNum[i][j] = 0;
				}
			}
		}
		
		//---- Tracing borderlines
		PointD aPoint;
		IJPoint aijPoint;
		BorderLine aBLine;
		List<PointD> pointList;
		List<IJPoint> ijPList;
		int sI, sJ, i1, j1, i2, j2, i3 = 0, j3 = 0;
		for (i = 1; i < m + 1; i++) {
			for (j = 1; j < n + 1; j++) {
				if (S2[i][j] == 1) //Tracing border from any border point
				{
					pointList = new ArrayList<>();
					ijPList = new ArrayList<>();
					aPoint = new PointD();
					aPoint.X = X[i - 1][j - 1];
					aPoint.Y = Y[i - 1][j - 1];
					aijPoint = new IJPoint();
					aijPoint.I = i - 1;
					aijPoint.J = j - 1;
					pointList.add(aPoint);
					ijPList.add(aijPoint);
					sI = i;
					sJ = j;
					i2 = i;
					j2 = j;
					i1 = i2;
					j1 = -1;    //Trace from left firstly                        
					
					while (true) {
						int[] ij3 = new int[2];
						ij3[0] = i3;
						ij3[1] = j3;
						if (traceBorder(S2, i1, i2, j1, j2, ij3)) {
							i3 = ij3[0];
							j3 = ij3[1];
							i1 = i2;
							j1 = j2;
							i2 = i3;
							j2 = j3;
							UNum[i3][j3] = UNum[i3][j3] - 1;
							if (UNum[i3][j3] == 0) {
								S2[i3][j3] = 3;    //Used border point
							}
						} else {
							break;
						}
						
						aPoint = new PointD();
						aPoint.X = X[i3-1][j3 - 1];
						aPoint.Y = Y[i3 - 1][j3 - 1];
						aijPoint = new IJPoint();
						aijPoint.I = i3 - 1;
						aijPoint.J = j3 - 1;
						pointList.add(aPoint);
						ijPList.add(aijPoint);
						if (i3 == sI && j3 == sJ) {
							break;
						}
					}
					UNum[i][j] = UNum[i][j] - 1;
					if (UNum[i][j] == 0) {
						S2[i][j] = 3;    //Used border point
					}                        //UNum[i][j] = UNum[i][j] - 1;
					if (pointList.size() > 1) {
						aBLine = new BorderLine();
						aBLine.area = getExtentAndArea(pointList, aBLine.extent);
						aBLine.isOutLine = true;
						aBLine.isClockwise = true;
						aBLine.pointList = pointList;
						aBLine.ijPointList = ijPList;
						borderLines.add(aBLine);
					}
				}
			}
		}
		
		//---- Form borders
		List<Border> borders = new ArrayList<>();
		Border aBorder;
		BorderLine aLine, bLine;
		//---- Sort borderlines with area from small to big.
		//For inside border line analysis
		for (i = 1; i < borderLines.size(); i++) {
			aLine = borderLines.get(i);
			for (j = 0; j < i; j++) {
				bLine = borderLines.get(j);
				if (aLine.area > bLine.area) {
					borderLines.remove(i);
					borderLines.add(j, aLine);
					break;
				}
			}
		}
		List<BorderLine> lineList;
		if (borderLines.size() == 1) //Only one boder line
		{
			aLine = borderLines.get(0);
			if (!isClockwise(aLine.pointList)) {
				Collections.reverse(aLine.pointList);
				Collections.reverse(aLine.ijPointList);
			}
			aLine.isClockwise = true;
			lineList = new ArrayList<>();
			lineList.add(aLine);
			aBorder = new Border();
			aBorder.LineList = lineList;
			borders.add(aBorder);
		} else //muti border lines
		{
			for (i = 0; i < borderLines.size(); i++) {
				if (i == borderLines.size()) {
					break;
				}
				
				aLine = borderLines.get(i);
				if (!isClockwise(aLine.pointList)) {
					Collections.reverse(aLine.pointList);
					Collections.reverse(aLine.ijPointList);
				}
				aLine.isClockwise = true;
				lineList = new ArrayList<>();
				lineList.add(aLine);
				//Try to find the boder lines are inside of aLine.
				for (j = i + 1; j < borderLines.size(); j++) {
					if (j == borderLines.size()) {
						break;
					}
					
					bLine = borderLines.get(j);
					if (bLine.extent.xMin > aLine.extent.xMin && bLine.extent.xMax < aLine.extent.xMax
							&& bLine.extent.yMin > aLine.extent.yMin && bLine.extent.yMax < aLine.extent.yMax) {
						aPoint = bLine.pointList.get(0);
						if (pointInPolygon(aLine.pointList, aPoint)) //bLine is inside of aLine
						{
							bLine.isOutLine = false;
							if (isClockwise(bLine.pointList)) {
								Collections.reverse(bLine.pointList);
								Collections.reverse(bLine.ijPointList);
							}
							bLine.isClockwise = false;
							lineList.add(bLine);
							borderLines.remove(j);
							j = j - 1;
						}
					}
				}
				aBorder = new Border();
				aBorder.LineList = lineList;
				borders.add(aBorder);
			}
		}
		
		return borders;
	}
	
	/**
	 * Tracing contour lines from the grid data with undefine data
	 *
	 * @param S0 input grid data
	 * @param X X coordinate array
	 * @param Y Y coordinate array
	 * @param nc number of contour values
	 * @param contour contour value array
	 * @param undefData Undefine data
	 * @param borders borders
	 * @param S1 data flag array
	 * @return Contour line list
	 */
	public static List<PolyLine> tracingContourLines(double[][] S0, double[][] X, double[][] Y,
			int nc, double[] contour, double undefData, List<Border> borders, int[][] S1) {
		List<PolyLine> contourLines = createContourLines_UndefData(S0, X, Y, nc, contour, S1, undefData, borders);
		
		return contourLines;
	}
	
	/**
	 * Create contour lines from the grid data with undefine data
	 *
	 * @param S0 input grid data
	 * @param X X coordinate array
	 * @param Y Y coordinate array
	 * @param nc number of contour values
	 * @param contour contour value array
	 * @param S1 flag array
	 * @param undefData undefine data
	 * @param borders border line list
	 * @return contour line list
	 */
	private static List<PolyLine> createContourLines_UndefData(double[][] S0, double[][] X, double[][] Y,
			int nc, double[] contour, int[][] S1, double undefData, List<Border> borders) {
		List<PolyLine> contourLineList = new ArrayList<>();
		List<PolyLine> cLineList;
		int m, n, i, j;
		m = S0.length;    //---- Y
		n = S0[0].length;    //---- X
		
		//---- Add a small value to aviod the contour point as same as data point
		double dShift;
		dShift = contour[0] * 0.00001;
		if (dShift == 0) {
			dShift = 0.00001;
		}
		for (i = 0; i < m; i++) {
			for (j = 0; j < n; j++) {
				if (!(doubleEquals(S0[i][j], undefData))) //S0[i, j] = S0[i, j] + (contour[1] - contour[0]) * 0.0001;
				{
					S0[i][j] = S0[i][j] + dShift;
				}
			}
		}
		
		//---- Define if H S are border
		int[][][] SB = new int[2][m][n - 1], HB = new int[2][m - 1][n];   //---- Which border and trace direction
		for (i = 0; i < m; i++) {
			for (j = 0; j < n; j++) {
				if (j < n - 1) {
					SB[0][i][j] = -1;
					SB[1][i][j] = -1;
				}
				if (i < m - 1) {
					HB[0][i][j] = -1;
					HB[1][i][j] = -1;
				}
			}
		}
		Border aBorder;
		BorderLine aBLine;
		List<IJPoint> ijPList;
		int k, si, sj;
		IJPoint aijP, bijP;
		for (i = 0; i < borders.size(); i++) {
			aBorder = borders.get(i);
			for (j = 0; j < aBorder.getLineNum(); j++) {
				aBLine = aBorder.LineList.get(j);
				ijPList = aBLine.ijPointList;
				for (k = 0; k < ijPList.size() - 1; k++) {
					aijP = ijPList.get(k);
					bijP = ijPList.get(k + 1);
					if (aijP.I == bijP.I) {
						si = aijP.I;
						sj = Math.min(aijP.J, bijP.J);
						SB[0][si][sj] = i;
						if (bijP.J > aijP.J) //---- Trace from top
						{
							SB[1][si][sj] = 1;
						} else {
							SB[1][si][sj] = 0;    //----- Trace from bottom
						}
					} else {
						sj = aijP.J;
						si = Math.min(aijP.I, bijP.I);
						HB[0][si][sj] = i;
						if (bijP.I > aijP.I) //---- Trace from left
						{
							HB[1][si][sj] = 0;
						} else {
							HB[1][si][sj] = 1;    //---- Trace from right
						}
					}
				}
			}
		}
		
		//---- Define horizontal and vertical arrays with the position of the tracing value, -2 means no tracing point. 
		double[][] S = new double[m][n - 1];
		double[][] H = new double[m - 1][n];
		double w;    //---- Tracing value
		int c;
		//ArrayList _endPointList = new ArrayList();    //---- Contour line end points for insert to border
		for (c = 0; c < nc; c++) {
			w = contour[c];
			for (i = 0; i < m; i++) {
				for (j = 0; j < n; j++) {
					if (j < n - 1) {
						if (S1[i][j] != 0 && S1[i][j + 1] != 0) {
							if ((S0[i][j] - w) * (S0[i][j + 1] - w) < 0) //---- Has tracing value
							{
								S[i][j] = (w - S0[i][j]) / (S0[i][j + 1] - S0[i][j]);
							} else {
								S[i][j] = -2;
							}
						} else {
							S[i][j] = -2;
						}
					}
					if (i < m - 1) {
						if (S1[i][j] != 0 && S1[i + 1][j] != 0) {
							if ((S0[i][j] - w) * (S0[i + 1][j] - w) < 0) //---- Has tracing value
							{
								H[i][j] = (w - S0[i][j]) / (S0[i + 1][j] - S0[i][j]);
							} else {
								H[i][j] = -2;
							}
						} else {
							H[i][j] = -2;
						}
					}
				}
			}
			
			cLineList = isoline_UndefData(S0, X, Y, w, S, H, SB, HB, contourLineList.size());
			contourLineList.addAll(cLineList);
		}
		
		//---- Set border index for close contours
		PolyLine aLine;
		//List pList = new ArrayList();
		PointD aPoint;
		for (i = 0; i < borders.size(); i++) {
			aBorder = borders.get(i);
			aBLine = aBorder.LineList.get(0);
			for (j = 0; j < contourLineList.size(); j++) {
				aLine = contourLineList.get(j);
				if (aLine.Type.equals("Close")) {
					aPoint = aLine.PointList.get(0);
					if (pointInPolygon(aBLine.pointList, aPoint)) {
						aLine.BorderIdx = i;
					}
				}
				contourLineList.remove(j);
				contourLineList.add(j, aLine);
			}
		}
		
		return contourLineList;
	}
	
	private static List<PolyLine> isoline_UndefData(double[][] S0, double[][] X, double[][] Y,
			double W, double[][] S, double[][] H, int[][][] SB, int[][][] HB, int lineNum) {
		
		List<PolyLine> cLineList = new ArrayList<>();
		int m, n, i, j;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1, j2, i3 = 0, j3 = 0;
		double a2x, a2y, a3x = 0, a3y = 0, sx, sy;
		PointD aPoint;
		PolyLine aLine;
		List<PointD> pList;
		boolean isS = true;
		EndPoint aEndPoint = new EndPoint();
		//---- Tracing from border
		for (i = 0; i < m; i++) {
			for (j = 0; j < n; j++) {
				if (j < n - 1) {
					if (SB[0][i][j] > -1) //---- Border
					{
						if (S[i][j] != -2) {
							pList = new ArrayList<>();
							i2 = i;
							j2 = j;
							a2x = X[i2][j2] + S[i2][j2] * (X[i2][j2 + 1] - X[i2][j2]);    //---- x of first point
							a2y = Y[i2][j2];                   //---- y of first point
							if (SB[1][i][j] == 0) //---- Bottom border
							{
								i1 = -1;
								aEndPoint.sPoint.X = X[i][j + 1];
								aEndPoint.sPoint.Y = Y[i][j + 1];
							} else {
								i1 = i2;
								aEndPoint.sPoint.X = X[i][j];
								aEndPoint.sPoint.Y = Y[i][j];
							}
							j1 = j2;
							aPoint = new PointD();
							aPoint.X = a2x;
							aPoint.Y = a2y;
							pList.add(aPoint);
							
							aEndPoint.Index = lineNum + cLineList.size();
							aEndPoint.Point = aPoint;
							aEndPoint.BorderIdx = SB[0][i][j];
							_endPointList.add(aEndPoint);
							
							aLine = new PolyLine();
							aLine.Type = "Border";
							aLine.BorderIdx = SB[0][i][j];
							while (true) {
								int[] ij3 = {i3, j3};
								double[] a3xy = {a3x, a3y};
								boolean[] IsS = {isS};
								if (traceIsoline_UndefData(i1, i2, H, S, j1, j2, X, Y, a2x, ij3, a3xy, IsS)) {
									i3 = ij3[0];
									j3 = ij3[1];
									a3x = a3xy[0];
									a3y = a3xy[1];
									isS = IsS[0];
									aPoint = new PointD();
									aPoint.X = a3x;
									aPoint.Y = a3y;
									pList.add(aPoint);
									if (isS) {
										if (SB[0][i3][j3] > -1) {
											if (SB[1][i3][j3] == 0) {
												aEndPoint.sPoint.X = X[i3][j3 + 1];
												aEndPoint.sPoint.Y = Y[i3][j3 + 1];
											} else {
												aEndPoint.sPoint.X = X[i3][j3];
												aEndPoint.sPoint.Y = Y[i3][j3];
											}
											break;
										}
									} else if (HB[0][i3][j3] > -1) {
										if (HB[1][i3][j3] == 0) {
											aEndPoint.sPoint.X = X[i3][j3];
											aEndPoint.sPoint.Y = Y[i3][j3];
										} else {
											aEndPoint.sPoint.X = X[i3 + 1][j3];
											aEndPoint.sPoint.Y = Y[i3 + 1][j3];
										}
										break;
									}
									a2x = a3x;
									//a2y = a3y;
									i1 = i2;
									j1 = j2;
									i2 = i3;
									j2 = j3;
								} else {
									aLine.Type = "Error";
									break;
								}
							}
							S[i][j] = -2;
							if (pList.size() > 1 && !aLine.Type.equals("Error")) {
								aEndPoint.Point = aPoint;
								_endPointList.add(aEndPoint);
								
								aLine.Value = W;
								aLine.PointList = pList;
								cLineList.add(aLine);
							} else {
								_endPointList.remove(_endPointList.size() - 1);
							}
							
						}
					}
				}
				if (i < m - 1) {
					if (HB[0][i][j] > -1) //---- Border
					{
						if (H[i][j] != -2) {
							pList = new ArrayList<>();
							i2 = i;
							j2 = j;
							a2x = X[i2][j2];
							a2y = Y[i2][j2] + H[i2][j2] * (Y[i2 + 1][j2] - Y[i2][j2]);
							i1 = i2;
							if (HB[1][i][j] == 0) {
								j1 = -1;
								aEndPoint.sPoint.X = X[i][j];
								aEndPoint.sPoint.Y = Y[i][j];
							} else {
								j1 = j2;
								aEndPoint.sPoint.X = X[i + 1][j];
								aEndPoint.sPoint.Y = Y[i + 1][j];
							}
							aPoint = new PointD();
							aPoint.X = a2x;
							aPoint.Y = a2y;
							pList.add(aPoint);
							
							aEndPoint.Index = lineNum + cLineList.size();
							aEndPoint.Point = aPoint;
							aEndPoint.BorderIdx = HB[0][i][j];
							_endPointList.add(aEndPoint);
							
							aLine = new PolyLine();
							aLine.Type = "Border";
							aLine.BorderIdx = HB[0][i][j];
							while (true) {
								int[] ij3 = {i3, j3};
								double[] a3xy = {a3x, a3y};
								boolean[] IsS = {isS};
								if (traceIsoline_UndefData(i1, i2, H, S, j1, j2, X, Y, a2x, ij3, a3xy, IsS)) {
									i3 = ij3[0];
									j3 = ij3[1];
									a3x = a3xy[0];
									a3y = a3xy[1];
									isS = IsS[0];
									aPoint = new PointD();
									aPoint.X = a3x;
									aPoint.Y = a3y;
									pList.add(aPoint);
									if (isS) {
										if (SB[0][i3][j3] > -1) {
											if (SB[1][i3][j3] == 0) {
												aEndPoint.sPoint.X = X[i3][j3 + 1];
												aEndPoint.sPoint.Y = Y[i3][j3 + 1];
											} else {
												aEndPoint.sPoint.X = X[i3][j3];
												aEndPoint.sPoint.Y = Y[i3][j3];
											}
											break;
										}
									} else if (HB[0][i3][j3] > -1) {
										if (HB[1][i3][j3] == 0) {
											aEndPoint.sPoint.X = X[i3][j3];
											aEndPoint.sPoint.Y = Y[i3][j3];
										} else {
											aEndPoint.sPoint.X = X[i3 + 1][j3];
											aEndPoint.sPoint.Y = Y[i3 + 1][j3];
										}
										break;
									}
									a2x = a3x;
									//a2y = a3y;
									i1 = i2;
									j1 = j2;
									i2 = i3;
									j2 = j3;
								} else {
									aLine.Type = "Error";
									break;
								}
							}
							H[i][j] = -2;
							if (pList.size() > 1 && !aLine.Type.equals("Error")) {
								aEndPoint.Point = aPoint;
								_endPointList.add(aEndPoint);
								
								aLine.Value = W;
								aLine.PointList = pList;
								cLineList.add(aLine);
							} else {
								_endPointList.remove(_endPointList.size() - 1);
							}
							
						}
					}
				}
			}
		}
		
		//---- Clear border points
		for (j = 0; j < n - 1; j++) {
			if (S[0][j] != -2) {
				S[0][j] = -2;
			}
			if (S[m - 1][j] != -2) {
				S[m - 1][j] = -2;
			}
		}
		
		for (i = 0; i < m - 1; i++) {
			if (H[i][0] != -2) {
				H[i][0] = -2;
			}
			if (H[i][n - 1] != -2) {
				H[i][n - 1] = -2;
			}
		}
		
		//---- Tracing close lines
		for (i = 1; i < m - 2; i++) {
			for (j = 1; j < n - 1; j++) {
				if (H[i][j] != -2) {
					List<PointD> pointList = new ArrayList<>();
					i2 = i;
					j2 = j;
					a2x = X[i][j2];
					a2y = Y[i][j2] + H[i][j2] * (Y[i + 1][j2] - Y[i][j2]);
					j1 = -1;
					i1 = i2;
					sx = a2x;
					sy = a2y;
					aPoint = new PointD();
					aPoint.X = a2x;
					aPoint.Y = a2y;
					pointList.add(aPoint);
					aLine = new PolyLine();
					aLine.Type = "Close";
					
					while (true) {
						int[] ij3 = new int[2];
						double[] a3xy = new double[2];
						boolean[] IsS = new boolean[1];
						if (traceIsoline_UndefData(i1, i2, H, S, j1, j2, X, Y, a2x, ij3, a3xy, IsS)) {
							i3 = ij3[0];
							j3 = ij3[1];
							a3x = a3xy[0];
							a3y = a3xy[1];
							//isS = IsS[0];
							aPoint = new PointD();
							aPoint.X = a3x;
							aPoint.Y = a3y;
							pointList.add(aPoint);
							if (Math.abs(a3y - sy) < 0.000001 && Math.abs(a3x - sx) < 0.000001) {
								break;
							}
							
							a2x = a3x;
							//a2y = a3y;
							i1 = i2;
							j1 = j2;
							i2 = i3;
							j2 = j3;
							//If X[j2] < a2x && i2 = 0 )
							//    aLine.type = "Error"
							//    Exit Do
							//End If
						} else {
							aLine.Type = "Error";
							break;
						}
					}
					H[i][j] = -2;
					if (pointList.size() > 1 && !aLine.Type.equals("Error")) {
						aLine.Value = W;
						aLine.PointList = pointList;
						cLineList.add(aLine);
					}
				}
			}
		}
		
		for (i = 1; i < m - 1; i++) {
			for (j = 1; j < n - 2; j++) {
				if (S[i][j] != -2) {
					List<PointD> pointList = new ArrayList<>();
					i2 = i;
					j2 = j;
					a2x = X[i][j2] + S[i][j] * (X[i][j2 + 1] - X[i][j2]);
					a2y = Y[i][j2];
					j1 = j2;
					i1 = -1;
					sx = a2x;
					sy = a2y;
					aPoint = new PointD();
					aPoint.X = a2x;
					aPoint.Y = a2y;
					pointList.add(aPoint);
					aLine = new PolyLine();
					aLine.Type = "Close";
					
					while (true) {
						int[] ij3 = new int[2];
						double[] a3xy = new double[2];
						boolean[] IsS = new boolean[1];
						if (traceIsoline_UndefData(i1, i2, H, S, j1, j2, X, Y, a2x, ij3, a3xy, IsS)) {
							i3 = ij3[0];
							j3 = ij3[1];
							a3x = a3xy[0];
							a3y = a3xy[1];
							//isS = IsS[0];
							aPoint = new PointD();
							aPoint.X = a3x;
							aPoint.Y = a3y;
							pointList.add(aPoint);
							if (Math.abs(a3y - sy) < 0.000001 && Math.abs(a3x - sx) < 0.000001) {
								break;
							}
							
							a2x = a3x;
							//a2y = a3y;
							i1 = i2;
							j1 = j2;
							i2 = i3;
							j2 = j3;
						} else {
							aLine.Type = "Error";
							break;
						}
					}
					S[i][j] = -2;
					if (pointList.size() > 1 && !aLine.Type.equals("Error")) {
						aLine.Value = W;
						aLine.PointList = pointList;
						cLineList.add(aLine);
					}
				}
			}
		}
		
		return cLineList;
	}
	
	private static boolean doubleEquals(double a, double b) {
		double difference = Math.abs(a * 0.00001);
		if (Math.abs(a - b) <= difference) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean traceBorder(int[][] S1, int i1, int i2, int j1, int j2, int[] ij3) {
		boolean canTrace = true;
		int a, b, c, d;
		if (i1 < i2) //---- Trace from bottom
		{
			if (S1[i2][j2 - 1] == 1 && S1[i2][j2 + 1] == 1) {
				a = S1[i2 - 1][j2 - 1];
				b = S1[i2 + 1][j2];
				c = S1[i2 + 1][j2 - 1];
				if ((a != 0 && b == 0) || (a == 0 && b != 0 && c != 0)) {
					ij3[0] = i2;
					ij3[1] = j2 - 1;
				} else {
					ij3[0] = i2;
					ij3[1] = j2 + 1;
				}
			} else if (S1[i2][j2 - 1] == 1 && S1[i2 + 1][j2] == 1) {
				a = S1[i2 + 1][j2 - 1];
				b = S1[i2 + 1][j2 + 1];
				c = S1[i2][j2 - 1];
				d = S1[i2][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2;
						ij3[1] = j2 - 1;
					} else {
						ij3[0] = i2 + 1;
						ij3[1] = j2;
					}
				} else {
					ij3[0] = i2;
					ij3[1] = j2 - 1;
				}
			} else if (S1[i2][j2 + 1] == 1 && S1[i2 + 1][j2] == 1) {
				a = S1[i2 + 1][j2 - 1];
				b = S1[i2 + 1][j2 + 1];
				c = S1[i2][j2 - 1];
				d = S1[i2][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2;
						ij3[1] = j2 + 1;
					} else {
						ij3[0] = i2 + 1;
						ij3[1] = j2;
					}
				} else {
					ij3[0] = i2;
					ij3[1] = j2 + 1;
				}
			} else if (S1[i2][j2 - 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 - 1;
			} else if (S1[i2][j2 + 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 + 1;
			} else if (S1[i2 + 1][j2] == 1) {
				ij3[0] = i2 + 1;
				ij3[1] = j2;
			} else {
				canTrace = false;
			}
		} else if (j1 < j2) //---- Trace from left
		{
			if (S1[i2 + 1][j2] == 1 && S1[i2 - 1][j2] == 1) {
				a = S1[i2 + 1][j2 - 1];
				b = S1[i2][j2 + 1];
				c = S1[i2 + 1][j2 + 1];
				if ((a != 0 && b == 0) || (a == 0 && b != 0 && c != 0)) {
					ij3[0] = i2 + 1;
					ij3[1] = j2;
				} else {
					ij3[0] = i2 - 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 + 1][j2] == 1 && S1[i2][j2 + 1] == 1) {
				c = S1[i2 - 1][j2];
				d = S1[i2 + 1][j2];
				a = S1[i2 - 1][j2 + 1];
				b = S1[i2 + 1][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2 + 1;
						ij3[1] = j2;
					} else {
						ij3[0] = i2;
						ij3[1] = j2 + 1;
					}
				} else {
					ij3[0] = i2 + 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 - 1][j2] == 1 && S1[i2][j2 + 1] == 1) {
				c = S1[i2 - 1][j2];
				d = S1[i2 + 1][j2];
				a = S1[i2 - 1][j2 + 1];
				b = S1[i2 + 1][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2 - 1;
						ij3[1] = j2;
					} else {
						ij3[0] = i2;
						ij3[1] = j2 + 1;
					}
				} else {
					ij3[0] = i2 - 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 + 1][j2] == 1) {
				ij3[0] = i2 + 1;
				ij3[1] = j2;
			} else if (S1[i2 - 1][j2] == 1) {
				ij3[0] = i2 - 1;
				ij3[1] = j2;
			} else if (S1[i2][j2 + 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 + 1;
			} else {
				canTrace = false;
			}
		} else if (i1 > i2) //---- Trace from top
		{
			if (S1[i2][j2 - 1] == 1 && S1[i2][j2 + 1] == 1) {
				a = S1[i2 + 1][j2 - 1];
				b = S1[i2 - 1][j2];
				c = S1[i2 - 1][j2 + 1];
				if ((a != 0 && b == 0) || (a == 0 && b != 0 && c != 0)) {
					ij3[0] = i2;
					ij3[1] = j2 - 1;
				} else {
					ij3[0] = i2;
					ij3[1] = j2 + 1;
				}
			} else if (S1[i2][j2 - 1] == 1 && S1[i2 - 1][j2] == 1) {
				a = S1[i2 - 1][j2 - 1];
				b = S1[i2 - 1][j2 + 1];
				c = S1[i2][j2 - 1];
				d = S1[i2][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2;
						ij3[1] = j2 - 1;
					} else {
						ij3[0] = i2 - 1;
						ij3[1] = j2;
					}
				} else {
					ij3[0] = i2;
					ij3[1] = j2 - 1;
				}
			} else if (S1[i2][j2 + 1] == 1 && S1[i2 - 1][j2] == 1) {
				a = S1[i2 - 1][j2 - 1];
				b = S1[i2 - 1][j2 + 1];
				c = S1[i2][j2 - 1];
				d = S1[i2][j2 + 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2;
						ij3[1] = j2 + 1;
					} else {
						ij3[0] = i2 - 1;
						ij3[1] = j2;
					}
				} else {
					ij3[0] = i2;
					ij3[1] = j2 + 1;
				}
			} else if (S1[i2][j2 - 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 - 1;
			} else if (S1[i2][j2 + 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 + 1;
			} else if (S1[i2 - 1][j2] == 1) {
				ij3[0] = i2 - 1;
				ij3[1] = j2;
			} else {
				canTrace = false;
			}
		} else if (j1 > j2) //---- Trace from right
		{
			if (S1[i2 + 1][j2] == 1 && S1[i2 - 1][j2] == 1) {
				a = S1[i2 + 1][j2 + 1];
				b = S1[i2][j2 - 1];
				c = S1[i2 - 1][j2 - 1];
				if ((a != 0 && b == 0) || (a == 0 && b != 0 && c != 0)) {
					ij3[0] = i2 + 1;
					ij3[1] = j2;
				} else {
					ij3[0] = i2 - 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 + 1][j2] == 1 && S1[i2][j2 - 1] == 1) {
				c = S1[i2 - 1][j2];
				d = S1[i2 + 1][j2];
				a = S1[i2 - 1][j2 - 1];
				b = S1[i2 + 1][j2 - 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2 + 1;
						ij3[1] = j2;
					} else {
						ij3[0] = i2;
						ij3[1] = j2 - 1;
					}
				} else {
					ij3[0] = i2 + 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 - 1][j2] == 1 && S1[i2][j2 - 1] == 1) {
				c = S1[i2 - 1][j2];
				d = S1[i2 + 1][j2];
				a = S1[i2 - 1][j2 - 1];
				b = S1[i2 + 1][j2 - 1];
				if (a == 0 || b == 0 || c == 0 || d == 0) {
					if ((a == 0 && d == 0) || (b == 0 && c == 0)) {
						ij3[0] = i2 - 1;
						ij3[1] = j2;
					} else {
						ij3[0] = i2;
						ij3[1] = j2 - 1;
					}
				} else {
					ij3[0] = i2 - 1;
					ij3[1] = j2;
				}
			} else if (S1[i2 + 1][j2] == 1) {
				ij3[0] = i2 + 1;
				ij3[1] = j2;
			} else if (S1[i2 - 1][j2] == 1) {
				ij3[0] = i2 - 1;
				ij3[1] = j2;
			} else if (S1[i2][j2 - 1] == 1) {
				ij3[0] = i2;
				ij3[1] = j2 - 1;
			} else {
				canTrace = false;
			}
		}
		
		return canTrace;
	}
	
	private static double getExtentAndArea(List<PointD> pList, Extent aExtent) {
		double bArea, minX, minY, maxX, maxY;
		int i;
		PointD aPoint;
		aPoint = pList.get(0);
		minX = aPoint.X;
		maxX = aPoint.X;
		minY = aPoint.Y;
		maxY = aPoint.Y;
		for (i = 1; i < pList.size(); i++) {
			aPoint = pList.get(i);
			if (aPoint.X < minX) {
				minX = aPoint.X;
			}
			
			if (aPoint.X > maxX) {
				maxX = aPoint.X;
			}
			
			if (aPoint.Y < minY) {
				minY = aPoint.Y;
			}
			
			if (aPoint.Y > maxY) {
				maxY = aPoint.Y;
			}
		}
		
		aExtent.xMin = minX;
		aExtent.yMin = minY;
		aExtent.xMax = maxX;
		aExtent.yMax = maxY;
		bArea = (maxX - minX) * (maxY - minY);
		
		return bArea;
	}
	
	/**
	 * Create contour lines
	 *
	 * @param S0 input grid data array
	 * @param X X coordinate array
	 * @param Y Y coordinate array
	 * @param nc number of contour values
	 * @param contour contour value array
	 * @param nx Interval of X coordinate
	 * @param ny Interval of Y coordinate
	 * @return contour lines
	 */
	private static List<PolyLine> createContourLines(double[][] S0, double[][] X, double[][] Y, int nc, double[] contour, double nx, double ny) {
		List<PolyLine> contourLineList = new ArrayList<>(), bLineList, lLineList,
				tLineList, rLineList, cLineList;
		int m, n, i, j;
		m = S0.length;    //---- Y
		n = S0[0].length;    //---- X
		
		//---- Define horizontal and vertical arrays with the position of the tracing value, -2 means no tracing point. 
		double[][] S = new double[m][n - 1], H = new double[m - 1][n];
		double dShift;
		dShift = contour[0] * 0.00001;
		if (dShift == 0) {
			dShift = 0.00001;
		}
		for (i = 0; i < m; i++) {
			for (j = 0; j < n; j++) {
				S0[i][j] = S0[i][j] + dShift;
			}
		}
		
		double w;    //---- Tracing value
		int c;
		for (c = 0; c < nc; c++) {
			w = contour[c];
			for (i = 0; i < m; i++) {
				for (j = 0; j < n; j++) {
					if (j < n - 1) {
						if ((S0[i][j] - w) * (S0[i][j + 1] - w) < 0) //---- Has tracing value
						{
							S[i][j] = (w - S0[i][j]) / (S0[i][j + 1] - S0[i][j]);
						} else {
							S[i][j] = -2;
						}
					}
					if (i < m - 1) {
						if ((S0[i][j] - w) * (S0[i + 1][j] - w) < 0) //---- Has tracing value
						{
							H[i][j] = (w - S0[i][j]) / (S0[i + 1][j] - S0[i][j]);
						} else {
							H[i][j] = -2;
						}
					}
				}
			}
			
			bLineList = isoline_Bottom(S0, X, Y, w, nx, ny, S, H);
			lLineList = isoline_Left(S0, X, Y, w, nx, ny, S, H);
			tLineList = isoline_Top(S0, X, Y, w, nx, ny, S, H);
			rLineList = isoline_Right(S0, X, Y, w, nx, ny, S, H);
			cLineList = isoline_Close(S0, X, Y, w, nx, ny, S, H);
			contourLineList.addAll(bLineList);
			contourLineList.addAll(lLineList);
			contourLineList.addAll(tLineList);
			contourLineList.addAll(rLineList);
			contourLineList.addAll(cLineList);
		}
		
		return contourLineList;
	}
	
	private static List<PolyLine> isoline_Bottom(double[][] S0, double[][] X, double[][] Y, double W, double nx, double ny,
			double[][] S, double[][] H) {
		List<PolyLine> bLineList = new ArrayList<>();
		int m, n, j;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1 = 0, j2, i3, j3;
		double a2x, a2y, a3x, a3y;
		Object[] returnVal;
		PointD aPoint = new PointD();
		PolyLine aLine = new PolyLine();
		for (j = 0; j < n - 1; j++) //---- Trace isoline from bottom
		{
			if (S[0][j] != -2) //---- Has tracing value
			{
				List<PointD> pointList = new ArrayList<>();
				i2 = 0;
				j2 = j;
				a2x = X[0][j] + S[0][j] * nx;    //---- x of first point
				a2y = Y[0][0];                   //---- y of first point
				i1 = -1;
				aPoint.X = a2x;
				aPoint.Y = a2y;
				pointList.add(aPoint);
				while (true) {
					returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
					i3 = Integer.parseInt(returnVal[0].toString());
					j3 = Integer.parseInt(returnVal[1].toString());
					a3x = Double.parseDouble(returnVal[2].toString());
					a3y = Double.parseDouble(returnVal[3].toString());
					aPoint.X = a3x;
					aPoint.Y = a3y;
					pointList.add(aPoint);
					if (i3 == m - 1 || j3 == n - 1 || a3y == Y[0][0] || a3x == X[0][0]) {
						break;
					}
					
					a2x = a3x;
					//a2y = a3y;
					i1 = i2;
					j1 = j2;
					i2 = i3;
					j2 = j3;
				}
				S[0][j] = -2;
				if (pointList.size() > 4) {
					aLine.Value = W;
					aLine.Type = "Bottom";
					aLine.PointList = new ArrayList<>(pointList);
					//m_LineList.Add(aLine);
					bLineList.add(aLine);
				}
			}
		}
		
		return bLineList;
	}
	
	private static Object[] traceIsoline(int i1, int i2, double[][] H, double[][] S, int j1, int j2, double[][] X,
			double[][] Y, double nx, double ny, double a2x) {
		int i3, j3;
		double a3x, a3y;
		if (i1 < i2) //---- Trace from bottom
		{
			if (H[i2][j2] != -2 && H[i2][j2 + 1] != -2) {
				if (H[i2][j2] < H[i2][j2 + 1]) {
					a3x = X[i2][j2];
					a3y = Y[i2][j2] + H[i2][j2] * ny;
					i3 = i2;
					j3 = j2;
					H[i3][j3] = -2;
				} else {
					a3x = X[i2][j2 + 1];
					a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * ny;
					i3 = i2;
					j3 = j2 + 1;
					H[i3][j3] = -2;
				}
			} else if (H[i2][j2] != -2 && H[i2][j2 + 1] == -2) {
				a3x = X[i2][j2];
				a3y = Y[i2][j2] + H[i2][j2] * ny;
				i3 = i2;
				j3 = j2;
				H[i3][j3] = -2;
			} else if (H[i2][j2] == -2 && H[i2][j2 + 1] != -2) {
				a3x = X[i2][j2 + 1];
				a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * ny;
				i3 = i2;
				j3 = j2 + 1;
				H[i3][j3] = -2;
			} else {
				a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * nx;
				a3y = Y[i2 + 1][j2];
				i3 = i2 + 1;
				j3 = j2;
				S[i3][j3] = -2;
			}
		} else if (j1 < j2) //---- Trace from left
		{
			if (S[i2][j2] != -2 && S[i2 + 1][j2] != -2) {
				if (S[i2][j2] < S[i2 + 1][j2]) {
					a3x = X[i2][j2] + S[i2][j2] * nx;
					a3y = Y[i2][j2];
					i3 = i2;
					j3 = j2;
					S[i3][j3] = -2;
				} else {
					a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * nx;
					a3y = Y[i2 + 1][j2];
					i3 = i2 + 1;
					j3 = j2;
					S[i3][j3] = -2;
				}
			} else if (S[i2][j2] != -2 && S[i2 + 1][j2] == -2) {
				a3x = X[i2][j2] + S[i2][j2] * nx;
				a3y = Y[i2][j2];
				i3 = i2;
				j3 = j2;
				S[i3][j3] = -2;
			} else if (S[i2][j2] == -2 && S[i2 + 1][j2] != -2) {
				a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * nx;
				a3y = Y[i2 + 1][j2];
				i3 = i2 + 1;
				j3 = j2;
				S[i3][j3] = -2;
			} else {
				a3x = X[i2][j2 + 1];
				a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * ny;
				i3 = i2;
				j3 = j2 + 1;
				H[i3][j3] = -2;
			}
		} else if (X[i2][j2] < a2x) //---- Trace from top
		{
			if (H[i2 - 1][j2] != -2 && H[i2 - 1][j2 + 1] != -2) {
				if (H[i2 - 1][j2] > H[i2 - 1][j2 + 1]) //---- < changed to >
				{
					a3x = X[i2 - 1][j2];
					a3y = Y[i2 - 1][j2] + H[i2 - 1][j2] * ny;
					i3 = i2 - 1;
					j3 = j2;
					H[i3][j3] = -2;
				} else {
					a3x = X[i2 - 1][j2 + 1];
					a3y = Y[i2 - 1][j2 + 1] + H[i2 - 1][j2 + 1] * ny;
					i3 = i2 - 1;
					j3 = j2 + 1;
					H[i3][j3] = -2;
				}
			} else if (H[i2 - 1][j2] != -2 && H[i2 - 1][j2 + 1] == -2) {
				a3x = X[i2 - 1][j2];
				a3y = Y[i2 - 1][j2] + H[i2 - 1][j2] * ny;
				i3 = i2 - 1;
				j3 = j2;
				H[i3][j3] = -2;
			} else if (H[i2 - 1][j2] == -2 && H[i2 - 1][j2 + 1] != -2) {
				a3x = X[i2 - 1][j2 + 1];
				a3y = Y[i2 - 1][j2 + 1] + H[i2 - 1][j2 + 1] * ny;
				i3 = i2 - 1;
				j3 = j2 + 1;
				H[i3][j3] = -2;
			} else {
				a3x = X[i2 - 1][j2] + S[i2 - 1][j2] * nx;
				a3y = Y[i2 - 1][j2];
				i3 = i2 - 1;
				j3 = j2;
				S[i3][j3] = -2;
			}
		} else //---- Trace from right
		{
			if (S[i2 + 1][j2 - 1] != -2 && S[i2][j2 - 1] != -2) {
				if (S[i2 + 1][j2 - 1] > S[i2][j2 - 1]) //---- < changed to >
				{
					a3x = X[i2 + 1][j2 - 1] + S[i2 + 1][j2 - 1] * nx;
					a3y = Y[i2 + 1][j2 - 1];
					i3 = i2 + 1;
					j3 = j2 - 1;
					S[i3][j3] = -2;
				} else {
					a3x = X[i2][j2 - 1] + S[i2][j2 - 1] * nx;
					a3y = Y[i2][j2 - 1];
					i3 = i2;
					j3 = j2 - 1;
					S[i3][j3] = -2;
				}
			} else if (S[i2 + 1][j2 - 1] != -2 && S[i2][j2 - 1] == -2) {
				a3x = X[i2 + 1][j2 - 1] + S[i2 + 1][j2 - 1] * nx;
				a3y = Y[i2 + 1][j2 - 1];
				i3 = i2 + 1;
				j3 = j2 - 1;
				S[i3][j3] = -2;
			} else if (S[i2 + 1][j2 - 1] == -2 && S[i2][j2 - 1] != -2) {
				a3x = X[i2][j2 - 1] + S[i2][j2 - 1] * nx;
				a3y = Y[i2][j2 - 1];
				i3 = i2;
				j3 = j2 - 1;
				S[i3][j3] = -2;
			} else {
				a3x = X[i2][j2 - 1];
				a3y = Y[i2][j2 - 1] + H[i2][j2 - 1] * ny;
				i3 = i2;
				j3 = j2 - 1;
				H[i3][j3] = -2;
			}
		}
		
		return new Object[]{i3, j3, a3x, a3y};
	}
	
	private static List<PolyLine> isoline_Left(double[][] S0, double[][] X, double[][] Y, double W, double nx, double ny,
			double[][] S, double[][] H) {
		List<PolyLine> lLineList = new ArrayList<>();
		int m, n, i;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1, j2, i3, j3;
		double a2x, a2y, a3x, a3y;
		Object[] returnVal;
		PointD aPoint = new PointD();
		PolyLine aLine = new PolyLine();
		for (i = 0; i < m - 1; i++) //---- Trace isoline from Left
		{
			if (H[i][0] != -2) {
				List<PointD> pointList = new ArrayList<>();
				i2 = i;
				j2 = 0;
				a2x = X[i][0];
				a2y = Y[i][0] + H[i][0] * ny;
				j1 = -1;
				i1 = i2;
				aPoint.X = a2x;
				aPoint.Y = a2y;
				pointList.add(aPoint);
				while (true) {
					returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
					i3 = Integer.parseInt(returnVal[0].toString());
					j3 = Integer.parseInt(returnVal[1].toString());
					a3x = Double.parseDouble(returnVal[2].toString());
					a3y = Double.parseDouble(returnVal[3].toString());
					aPoint.X = a3x;
					aPoint.Y = a3y;
					pointList.add(aPoint);
					if (i3 == m - 1 || j3 == n - 1 || a3y == Y[0][0] || a3x == X[0][0]) {
						break;
					}
					
					a2x = a3x;
					//a2y = a3y;
					i1 = i2;
					j1 = j2;
					i2 = i3;
					j2 = j3;
				}
				if (pointList.size() > 4) {
					aLine.Value = W;
					aLine.Type = "Left";
					aLine.PointList = new ArrayList<>(pointList);
					//m_LineList.Add(aLine);
					lLineList.add(aLine);
				}
			}
		}
		
		return lLineList;
	}
	
	private static List<PolyLine> isoline_Top(double[][] S0, double[][] X, double[][] Y, double W, double nx, double ny,
			double[][] S, double[][] H) {
		List<PolyLine> tLineList = new ArrayList<>();
		int m, n, j;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1, j2, i3, j3;
		double a2x, a2y, a3x, a3y;
		Object[] returnVal;
		PointD aPoint = new PointD();
		PolyLine aLine = new PolyLine();
		for (j = 0; j < n - 1; j++) {
			if (S[m - 1][j] != -2) {
				List<PointD> pointList = new ArrayList<>();
				i2 = m - 1;
				j2 = j;
				a2x = X[i2][j] + S[i2][j] * nx;
				a2y = Y[i2][j];
				i1 = i2;
				j1 = j2;
				aPoint.X = a2x;
				aPoint.Y = a2y;
				pointList.add(aPoint);
				while (true) {
					returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
					i3 = Integer.parseInt(returnVal[0].toString());
					j3 = Integer.parseInt(returnVal[1].toString());
					a3x = Double.parseDouble(returnVal[2].toString());
					a3y = Double.parseDouble(returnVal[3].toString());
					aPoint.X = a3x;
					aPoint.Y = a3y;
					pointList.add(aPoint);
					if (i3 == m - 1 || j3 == n - 1 || a3y == Y[0][0] || a3x == X[0][0]) {
						break;
					}
					
					a2x = a3x;
					//a2y = a3y;
					i1 = i2;
					j1 = j2;
					i2 = i3;
					j2 = j3;
				}
				S[m - 1][j] = -2;
				if (pointList.size() > 4) {
					aLine.Value = W;
					aLine.Type = "Top";
					aLine.PointList = new ArrayList<>(pointList);
					//m_LineList.Add(aLine);
					tLineList.add(aLine);
				}
			}
		}
		
		return tLineList;
	}
	
	private static List<PolyLine> isoline_Right(double[][] S0, double[][] X, double[][] Y, double W, double nx, double ny,
			double[][] S, double[][] H) {
		List<PolyLine> rLineList = new ArrayList<>();
		int m, n, i;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1, j2, i3, j3;
		double a2x, a2y, a3x, a3y;
		Object[] returnVal;
		PointD aPoint = new PointD();
		PolyLine aLine = new PolyLine();
		for (i = 0; i < m - 1; i++) {
			if (H[i][n - 1] != -2) {
				List<PointD> pointList = new ArrayList<>();
				i2 = i;
				j2 = n - 1;
				a2x = X[i][j2];
				a2y = Y[i][j2] + H[i][j2] * ny;
				j1 = j2;
				i1 = i2;
				aPoint.X = a2x;
				aPoint.Y = a2y;
				pointList.add(aPoint);
				while (true) {
					returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
					i3 = Integer.parseInt(returnVal[0].toString());
					j3 = Integer.parseInt(returnVal[1].toString());
					a3x = Double.parseDouble(returnVal[2].toString());
					a3y = Double.parseDouble(returnVal[3].toString());
					aPoint.X = a3x;
					aPoint.Y = a3y;
					pointList.add(aPoint);
					if (i3 == m - 1 || j3 == n - 1 || a3y == Y[0][0] || a3x == X[0][0]) {
						break;
					}
					
					a2x = a3x;
					//a2y = a3y;
					i1 = i2;
					j1 = j2;
					i2 = i3;
					j2 = j3;
				}
				if (pointList.size() > 4) {
					aLine.Value = W;
					aLine.Type = "Right";
					aLine.PointList = new ArrayList<>(pointList);
					rLineList.add(aLine);
				}
			}
		}
		
		return rLineList;
	}
	
	private static List<PolyLine> isoline_Close(double[][] S0, double[][] X, double[][] Y, double W, double nx, double ny,
			double[][] S, double[][] H) {
		List<PolyLine> cLineList = new ArrayList<>();
		int m, n, i, j;
		m = S0.length;
		n = S0[0].length;
		
		int i1, i2, j1, j2, i3, j3;
		double a2x, a2y, a3x, a3y, sx, sy;
		Object[] returnVal;
		PointD aPoint = new PointD();
		PolyLine aLine = new PolyLine();
		for (i = 1; i < m - 2; i++) {
			for (j = 1; j < n - 1; j++) {
				if (H[i][j] != -2) {
					List<PointD> pointList = new ArrayList<>();
					i2 = i;
					j2 = j;
					a2x = X[i][j2];
					a2y = Y[i][j2] + H[i][j2] * ny;
					j1 = 0;
					i1 = i2;
					sx = a2x;
					sy = a2y;
					aPoint.X = a2x;
					aPoint.Y = a2y;
					pointList.add(aPoint);
					while (true) {
						returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
						i3 = Integer.parseInt(returnVal[0].toString());
						j3 = Integer.parseInt(returnVal[1].toString());
						a3x = Double.parseDouble(returnVal[2].toString());
						a3y = Double.parseDouble(returnVal[3].toString());
						if (i3 == 0 && j3 == 0) {
							break;
						}
						
						aPoint.X = a3x;
						aPoint.Y = a3y;
						pointList.add(aPoint);
						if (Math.abs(a3y - sy) < 0.000001 && Math.abs(a3x - sx) < 0.000001) {
							break;
						}
						
						a2x = a3x;
						//a2y = a3y;
						i1 = i2;
						j1 = j2;
						i2 = i3;
						j2 = j3;
						if (i2 == m - 1 || j2 == n - 1) {
							break;
						}
						
					}
					H[i][j] = -2;
					if (pointList.size() > 4) {
						aLine.Value = W;
						aLine.Type = "Close";
						aLine.PointList = new ArrayList<>(pointList);
						cLineList.add(aLine);
					}
				}
			}
		}
		
		for (i = 1; i < m - 1; i++) {
			for (j = 1; j < n - 2; j++) {
				if (S[i][j] != -2) {
					List<PointD> pointList = new ArrayList<>();
					i2 = i;
					j2 = j;
					a2x = X[i][j2] + S[i][j] * nx;
					a2y = Y[i][j2];
					j1 = j2;
					i1 = 0;
					sx = a2x;
					sy = a2y;
					aPoint.X = a2x;
					aPoint.Y = a2y;
					pointList.add(aPoint);
					while (true) {
						returnVal = traceIsoline(i1, i2, H, S, j1, j2, X, Y, nx, ny, a2x);
						i3 = Integer.parseInt(returnVal[0].toString());
						j3 = Integer.parseInt(returnVal[1].toString());
						a3x = Double.parseDouble(returnVal[2].toString());
						a3y = Double.parseDouble(returnVal[3].toString());
						aPoint.X = a3x;
						aPoint.Y = a3y;
						pointList.add(aPoint);
						if (Math.abs(a3y - sy) < 0.000001 && Math.abs(a3x - sx) < 0.000001) {
							break;
						}
						
						a2x = a3x;
						i1 = i2;
						j1 = j2;
						i2 = i3;
						j2 = j3;
						if (i2 == m - 1 || j2 == n - 1) {
							break;
						}
					}
					S[i][j] = -2;
					if (pointList.size() > 4) {
						aLine.Value = W;
						aLine.Type = "Close";
						aLine.PointList = new ArrayList<>(pointList);
						//m_LineList.Add(aLine)
						cLineList.add(aLine);
					}
				}
			}
		}
		
		return cLineList;
	}
	
	private static boolean traceIsoline_UndefData(int i1, int i2, double[][] H, double[][] S, int j1, int j2, double[][] X,
			double[][] Y, double a2x, int[] ij3, double[] a3xy, boolean[] IsS) {
		boolean canTrace = true;
		double a3x = 0, a3y = 0;
		int i3 = 0, j3 = 0;
		boolean isS = true;
		if (i1 < i2) //---- Trace from bottom
		{
			if (H[i2][j2] != -2 && H[i2][j2 + 1] != -2) {
				if (H[i2][j2] < H[i2][j2 + 1]) {
					a3x = X[i2][j2];
					a3y = Y[i2][j2] + H[i2][j2] * (Y[i2 + 1][j2] - Y[i2][j2]);
					i3 = i2;
					j3 = j2;
					H[i3][j3] = -2;
					isS = false;
				} else {
					a3x = X[i2][j2 + 1];
					a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * (Y[i2 + 1][j2 + 1] - Y[i2][j2 + 1]);
					i3 = i2;
					j3 = j2 + 1;
					H[i3][j3] = -2;
					isS = false;
				}
			} else if (H[i2][j2] != -2 && H[i2][j2 + 1] == -2) {
				a3x = X[i2][j2];
				a3y = Y[i2][j2] + H[i2][j2] * (Y[i2 + 1][j2] - Y[i2][j2]);
				i3 = i2;
				j3 = j2;
				H[i3][j3] = -2;
				isS = false;
			} else if (H[i2][j2] == -2 && H[i2][j2 + 1] != -2) {
				a3x = X[i2][j2 + 1];
				a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * (Y[i2 + 1][j2 + 1] - Y[i2][j2 + 1]);
				i3 = i2;
				j3 = j2 + 1;
				H[i3][j3] = -2;
				isS = false;
			} else if (S[i2 + 1][j2] != -2) {
				a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * (X[i2 + 1][j2 + 1] - X[i2 + 1][j2]);
				a3y = Y[i2 + 1][j2];
				i3 = i2 + 1;
				j3 = j2;
				S[i3][j3] = -2;
				isS = true;
			} else {
				canTrace = false;
			}
		} else if (j1 < j2) //---- Trace from left
		{
			if (S[i2][j2] != -2 && S[i2 + 1][j2] != -2) {
				if (S[i2][j2] < S[i2 + 1][j2]) {
					a3x = X[i2][j2] + S[i2][j2] * (X[i2][j2 + 1] - X[i2][j2]);
					a3y = Y[i2][j2];
					i3 = i2;
					j3 = j2;
					S[i3][j3] = -2;
					isS = true;
				} else {
					a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * (X[i2 + 1][j2 + 1] - X[i2 + 1][j2]);
					a3y = Y[i2 + 1][j2];
					i3 = i2 + 1;
					j3 = j2;
					S[i3][j3] = -2;
					isS = true;
				}
			} else if (S[i2][j2] != -2 && S[i2 + 1][j2] == -2) {
				a3x = X[i2][j2] + S[i2][j2] * (X[i2][j2 + 1] - X[i2][j2]);
				a3y = Y[i2][j2];
				i3 = i2;
				j3 = j2;
				S[i3][j3] = -2;
				isS = true;
			} else if (S[i2][j2] == -2 && S[i2 + 1][j2] != -2) {
				a3x = X[i2 + 1][j2] + S[i2 + 1][j2] * (X[i2 + 1][j2 + 1] - X[i2 + 1][j2]);
				a3y = Y[i2 + 1][j2];
				i3 = i2 + 1;
				j3 = j2;
				S[i3][j3] = -2;
				isS = true;
			} else if (H[i2][j2 + 1] != -2) {
				a3x = X[i2][j2 + 1];
				a3y = Y[i2][j2 + 1] + H[i2][j2 + 1] * (Y[i2 + 1][j2 + 1] - Y[i2][j2 + 1]);
				i3 = i2;
				j3 = j2 + 1;
				H[i3][j3] = -2;
				isS = false;
			} else {
				canTrace = false;
			}
			
		} else if (X[i2][j2] < a2x) //---- Trace from top
		{
			if (H[i2 - 1][j2] != -2 && H[i2 - 1][j2 + 1] != -2) {
				if (H[i2 - 1][j2] > H[i2 - 1][j2 + 1]) //---- < changed to >
				{
					a3x = X[i2 - 1][j2];
					a3y = Y[i2 - 1][j2] + H[i2 - 1][j2] * (Y[i2][j2] - Y[i2 - 1][j2]);
					i3 = i2 - 1;
					j3 = j2;
					H[i3][j3] = -2;
					isS = false;
				} else {
					a3x = X[i2 - 1][j2 + 1];
					a3y = Y[i2 - 1][j2 + 1] + H[i2 - 1][j2 + 1] * (Y[i2][j2 + 1] - Y[i2 - 1][j2 + 1]);
					i3 = i2 - 1;
					j3 = j2 + 1;
					H[i3][j3] = -2;
					isS = false;
				}
			} else if (H[i2 - 1][j2] != -2 && H[i2 - 1][j2 + 1] == -2) {
				a3x = X[i2 - 1][j2];
				a3y = Y[i2 - 1][j2] + H[i2 - 1][j2] * (Y[i2][j2] - Y[i2 - 1][j2]);
				i3 = i2 - 1;
				j3 = j2;
				H[i3][j3] = -2;
				isS = false;
			} else if (H[i2 - 1][j2] == -2 && H[i2 - 1][j2 + 1] != -2) {
				a3x = X[i2 - 1][j2 + 1];
				a3y = Y[i2 - 1][j2 + 1] + H[i2 - 1][j2 + 1] * (Y[i2][j2 + 1] - Y[i2 - 1][j2 + 1]);
				i3 = i2 - 1;
				j3 = j2 + 1;
				H[i3][j3] = -2;
				isS = false;
			} else if (S[i2 - 1][j2] != -2) {
				a3x = X[i2 - 1][j2] + S[i2 - 1][j2] * (X[i2 - 1][j2 + 1] - X[i2 - 1][j2]);
				a3y = Y[i2 - 1][j2];
				i3 = i2 - 1;
				j3 = j2;
				S[i3][j3] = -2;
				isS = true;
			} else {
				canTrace = false;
			}
		} else //---- Trace from right
		{
			if (S[i2 + 1][j2 - 1] != -2 && S[i2][j2 - 1] != -2) {
				if (S[i2 + 1][j2 - 1] > S[i2][j2 - 1]) //---- < changed to >
				{
					a3x = X[i2 + 1][j2 - 1] + S[i2 + 1][j2 - 1] * (X[i2 + 1][j2] - X[i2 + 1][j2 - 1]);
					a3y = Y[i2 + 1][j2 - 1];
					i3 = i2 + 1;
					j3 = j2 - 1;
					S[i3][j3] = -2;
					isS = true;
				} else {
					a3x = X[i2][j2 - 1] + S[i2][j2 - 1] * (X[i2][j2] - X[i2][j2 - 1]);
					a3y = Y[i2][j2 - 1];
					i3 = i2;
					j3 = j2 - 1;
					S[i3][j3] = -2;
					isS = true;
				}
			} else if (S[i2 + 1][j2 - 1] != -2 && S[i2][j2 - 1] == -2) {
				a3x = X[i2 + 1][j2 - 1] + S[i2 + 1][j2 - 1] * (X[i2 + 1][j2] - X[i2 + 1][j2 - 1]);
				a3y = Y[i2 + 1][j2 - 1];
				i3 = i2 + 1;
				j3 = j2 - 1;
				S[i3][j3] = -2;
				isS = true;
			} else if (S[i2 + 1][j2 - 1] == -2 && S[i2][j2 - 1] != -2) {
				a3x = X[i2][j2 - 1] + S[i2][j2 - 1] * (X[i2][j2] - X[i2][j2 - 1]);
				a3y = Y[i2][j2 - 1];
				i3 = i2;
				j3 = j2 - 1;
				S[i3][j3] = -2;
				isS = true;
			} else if (H[i2][j2 - 1] != -2) {
				a3x = X[i2][j2 - 1];
				a3y = Y[i2][j2 - 1] + H[i2][j2 - 1] * (Y[i2 + 1][j2 - 1] - Y[i2][j2 - 1]);
				i3 = i2;
				j3 = j2 - 1;
				H[i3][j3] = -2;
				isS = false;
			} else {
				canTrace = false;
			}
		}
		
		ij3[0] = i3;
		ij3[1] = j3;
		a3xy[0] = a3x;
		a3xy[1] = a3y;
		IsS[0] = isS;
		
		return canTrace;
	}

}
