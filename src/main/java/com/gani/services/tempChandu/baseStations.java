package com.gani.services.tempChandu;

import java.io.*;
import java.util.*;


public class baseStations {

    private float[][] matrix;
    private int totalCpesToBeCovered;
    private int sectorLimit;
    private int cpes;

    public int getTotalCpesToBeCovered() {
        return totalCpesToBeCovered;
    }

    public void setTotalCpesToBeCovered(int totalCpesToBeCovered) {
        this.totalCpesToBeCovered = totalCpesToBeCovered;
    }

    public int getSectorLimit() {
        return sectorLimit;
    }

    public void setSectorLimit(int sectorLimit) {
        this.sectorLimit = sectorLimit;
    }

    public int getCpes() {
        return cpes;
    }

    public void setCpes(int cpes) {
        this.cpes = cpes;
    }

    public int getSectors() {
        return sectors;
    }

    public void setSectors(int sectors) {
        this.sectors = sectors;
    }

    private int sectors;

    public int getTotalCpsToBeCovered() {
        return totalCpesToBeCovered;
    }

    public void setTotalCpsToBeCovered(int totalCpesToBeCovered) {
        this.totalCpesToBeCovered = totalCpesToBeCovered;
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[][] matrix) {
        this.matrix = matrix;
    }

    public Map<Integer,Integer> optimalSectors(){

        Map<Integer,Integer> cpeSectorMap = new HashMap<>();//map of cpe and the sector it is assigned to
        Map<Integer,ArrayList<Integer>> tempCpeSectorMap;//temp map to store best possible sector for a given cpe
        int bestSector,cpesCovered=0,currentLimit=0;
        int[] sectorRankings = new int[matrix[0].length];
        float[][] bestSectorVsCPEs;
        boolean[] cpesalloted = new boolean[matrix.length];

        while(cpesCovered< totalCpesToBeCovered) {
        	tempCpeSectorMap = new HashMap<>();

            for (int cpe = 0; cpe < matrix.length; cpe++) {//for each cp, find the sector best suited for it
                if(cpesalloted[cpe]) {
                    continue;
                }

                bestSector = 0;
                for (int sector = 0; sector < matrix[0].length; sector++) {
                    if (matrix[cpe][sector] > matrix[cpe][bestSector])
                        bestSector = sector;
                }
                ArrayList<Integer> temp = tempCpeSectorMap.getOrDefault(bestSector,new ArrayList<Integer>());
                temp.add(cpe);

                tempCpeSectorMap.put(bestSector,temp);
                sectorRankings[bestSector]++;//updating the rankings of each sector by counting how many cpes it can serve the best
            }

            bestSector = getMaxSector(sectorRankings);//get the best sector for this iteration
            bestSectorVsCPEs = sortCpeMap(tempCpeSectorMap.get(bestSector),bestSector);

            currentLimit = Math.min(sectorLimit,bestSectorVsCPEs.length);

            int index=0;
            while(currentLimit>0 && cpesCovered++<totalCpesToBeCovered){
            	cpeSectorMap.put((int)bestSectorVsCPEs[index][1],bestSector);
                cpesalloted[(int)bestSectorVsCPEs[index][1]]=true;
                currentLimit--;
                index++;
            }

            //clean up
            Arrays.fill(sectorRankings, 0);//reset rankings
            removeSector(bestSector);//remove the sector from further iterations
        }
        return cpeSectorMap;
    }

    private float[][] sortCpeMap(ArrayList<Integer> cps, int bestSector) {//create 2D map of cpe index and cpe value and then sort
        float[][] tempMap = new float[cps.size()][2];
        int index=0;
        for (int cp:cps) {
            tempMap[index][0]=matrix[cp][bestSector];
            tempMap[index][1]=cp;
            index++;
        }
        tempMap = mySorter(tempMap);
        return tempMap;
    }

    private void removeSector(int bestSector) {
        for (int cp = 0; cp < matrix.length; cp++) {//remove the sector from further iterations
            matrix[cp][bestSector] = Integer.MIN_VALUE;
        }
    }

    private float[][] mySorter(float[][] map){//to sort cps based on their values
        Arrays.sort(map, (o1, o2) -> o1[0]<o2[0]? 1:-1);

        return map;
    }

    private int getMaxSector(int[] sectorRankings) {
        int maxIndex=0;
        for (int index=0;index<sectorRankings.length;index++) {
            if(sectorRankings[maxIndex]<sectorRankings[index])
                maxIndex=index;
        }
        return maxIndex;
    }
    
    public float[][] csvToArray (String fileName)
    {
    	float[][] matrix = null;
		int x=0, y=0;
    	try
        {
		BufferedReader in = new BufferedReader(new FileReader(fileName));

			String line;
			while ((line = in.readLine()) != null)	//file reading
			{
			   String[] values = line.split(",");
			   for (String str : values)
			   {
			      double str_double = Double.parseDouble(str);
			      matrix[x][y]=(float) str_double;
			      System.out.print(matrix[x][y] + " ");
			      y=y+1; //you have inserted a value to the former y, need to increment
			   }
			   x=x+1; // finished the row, need to increment the row number
			   System.out.println(""); // print a new row.
			   in.close();
			}
        }
    	catch( IOException ioException ) {}
		return matrix;
    }
    
    public static void main(String[] args)
    {
    	baseStations bs = new baseStations();
    	System.out.println("Enter the File path");
//    	System.out.println("Enter the number of CPEs and Sectors");
    	Scanner scan = new Scanner(System.in);
//    	bs.setCpes(scan.nextInt());
//    	bs.setSectors(scan.nextInt());
//    	System.out.println("Enter the path loss values");
//
//    	float[][] tempMatrix = new float[bs.getCpes()][bs.getSectors()];
//
//        for(int i = 0; i< bs.getCpes(); i++){
//            for(int j = 0 ;j< bs.getSectors(); j++){
//            	float value = scan.nextFloat();
//                tempMatrix[i][j] = value;
//            }  	
//        }




        bs.setMatrix(bs.csvToArray(scan.next()));

        System.out.println("Enter the coverage percent");
        int cov = scan.nextInt();
        bs.totalCpesToBeCovered = (int) ( Math.ceil((double)cov*bs.getCpes()/100));
        System.out.println("Enter the threshold limit for sector coverage");
        bs.sectorLimit = scan.nextInt();
        Map<Integer,Integer> myMap= bs.optimalSectors();

        for (Map.Entry entry:myMap.entrySet()) {
            System.out.println("CPE "+((int)entry.getKey()+1)+" is assigned to sector "+((int)entry.getValue()+1));
        }
        scan.close();
    }
}
