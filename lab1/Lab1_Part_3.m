clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading the data and putting the first 100000 entries in variables 
%Note that time is in seconds and framesize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
no_entries=100000;
[time1, framesize1] = textread('Bel.data', '%f %f');
time=time1(1:no_entries);
framesize=framesize1(1:no_entries);
%%%%%%%%%%%%%%%%%%%%%%%%%Exercise %%%3.2%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure(1);
jj=1;
i=1;
initial_p=0;
ag_time=1;
bytes_p=zeros(1,100);
while time(jj)<=initial_p
    jj=jj+1;
end
while i<=100
while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)
bytes_p(i)=bytes_p(i)+framesize(jj);
jj=jj+1;
end
i=i+1;
end
%%%%%%%%
subplot(3,1,1);bar(bytes_p);




