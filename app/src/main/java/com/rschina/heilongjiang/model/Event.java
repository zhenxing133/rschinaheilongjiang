package com.rschina.heilongjiang.model;


/**
 * Created by Administrator on 2016/9/22.
 */
public class Event {

    public final static  Action OK=new Action(0x1000001);
    public final static  Action Finish=new Action(0x1000002);
    public final static  Action Failed=new Action(0x1000003);
    public final static  Action NetError=new Action(0x1000004);
    public final static  Action IoError=new Action(0x1000005);
    public final static  Action DbError=new Action(0x1000006);
    public final static  Action Progress=new Action(0x1000007);
    public final static  Action Start=new Action(0x1000008);
    public final static  Action Stop=new Action(0x1000009);

    public static class Action{
        private int actionCode;
        public Action(int actionCode){
            this.actionCode=actionCode;
        }

        @Override
        public boolean equals(Object o) {
            if(this==o){
                return true;
            }else if(o instanceof Action){
                return  this.actionCode==((Action)o).actionCode;
            }
            return false;
        }
        public boolean withCode(int actionCode){return  this.actionCode==actionCode;}
    }

    public  interface ActionListener{
        void  onAction(Action action);
    }

}
