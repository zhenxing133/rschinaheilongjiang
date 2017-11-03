package com.chinars.mapapi.utils;

import java.util.Arrays;

public class IntSet {
	private int maxSize=10;
	private int size=0;
	private int[] data;
	public IntSet(){
		data=new int[maxSize];
	}
	
	public IntSet(int initCap){
		if(initCap>0){
			maxSize=initCap;
			data=new int[maxSize];
		}
	}
	
	public boolean add(int key){
		int index=Arrays.binarySearch(data,0,size,key);
		if(index<0){
			index=-index-1;
			for(int i=size;i>index;i--){
				data[i]=data[i-1];
			}
			data[index]=key;
			size++;
			if(size>=maxSize){
				maxSize=maxSize*2;
				data=Arrays.copyOf(data, maxSize);
			}
			return true;
		}
		return false;
	}
	
	public boolean remove(int key){
		int index=Arrays.binarySearch(data,0,size,key);
		if(index>=0){
			for(int i=index;i<size-1;i++){
				data[i]=data[i+1];
			}
			size--;
			return true;
		}
		return false;
	}
	
	public  boolean exist(int key){
		return Arrays.binarySearch(data,0,size,key)>=0;
	}
	
	public int size(){
		return size;
	}
	
	public boolean isEmpty(){
		return size==0;
	}
}
