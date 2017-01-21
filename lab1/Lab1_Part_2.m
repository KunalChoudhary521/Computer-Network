clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from the file
%Note: - time is in miliseconds and framesize is in Bytes
%      - file is sorted in transmit sequence
%  Column 1:   index of frame (in display sequence)
%  Column 2:   time of frame in ms (in display sequence)
%  Column 3:   type of frame (I, P, B)
%  Column 4:   size of frame (in Bytes)
%  Column 5-7: not used
%
% Since we are interested in the transmit sequence we ignore Columns 1 and
% 2. So, we are only interested in the following columns: 
%       Column 3:  assigned to type_f
%       Column 4:   assigned to framesize_f
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[index, time, type_f, framesize_f, dummy1, dymmy2, dymmy3 ] = textread('movietrace.data', '%f %f %c %f %f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   CODE FOR EXERCISE 2.2   (version: Spring 2007)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Extracting the I,P,B frmes characteristics from the source file
%frame size of I frames  : framesize_I
%frame size of P frames  : framesize_p 
%frame size of B frames  : framesize_B
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

a=0;
b=0;
c=0;
for i=1:length(index)
    if type_f(i)=='I'
        a=a+1;
        framesize_I(a)=framesize_f(i);
    end
     if type_f(i)=='B'
         b=b+1;
         framesize_B(b)=framesize_f(i);
         end
     if type_f(i)=='P'
         c=c+1;
         framesize_P(c)=framesize_f(i);
     end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Hint1: You may use the MATLAB functions 'length()','mean()','max()','min()'.
%       which calculate the length,mean,max,min of a
%       vector (for example max(framesize_P) will give you the size of
%       largest P frame
%Hint2: Use the 'plot' function to graph the framesize as a function of the frame
%       sequence number. 
%Hint3: Use the function 'hist' to show the distribution of the frames. Before 
%       that function type 'figure(2);' to indicate your figure number.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Exercise 2.2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
total_frames = length(index);

total_bytes = sum(framesize_f);

smallest_frame = min(framesize_f);
largest_frame = max(framesize_f);
mean_framesize = total_bytes / total_frames;

smallest_Iframe = min(framesize_I);
largest_Iframe = max(framesize_I);
mean_Iframe = mean(framesize_I);

smallest_Bframe = min(framesize_B);
largest_Bframe = max(framesize_B);
mean_Bframe = mean(framesize_B);

smallest_Pframe = min(framesize_P);
largest_Pframe = max(framesize_P);
mean_Pframe = mean(framesize_P);

frame_duration = (1/30); %because fps = 30, so frame duration is the inverse of that
mean_bit_rate = mean(framesize_f * 8) / frame_duration;  

peak_bit_rate = max(framesize_f * 8) / frame_duration;  

peak_to_avg_ratio = peak_bit_rate / mean_bit_rate;   %mean is average

figure(1);  %creates a separate window of graphs for part 2.2
%subplot(3,1,1);
plot(index, framesize_f);   %use plot
title('Video Trace - Packet Traffic');
xlabel('Frame Sequence Numbers');
ylabel('Frame Size (Bytes)');

hist_bins = 15;
figure(2);
subplot(3,1,1);
hist(framesize_I, hist_bins);	%use small # of bins (2nd argument) so it is easier to view results in the report
title('Distribution of I frames');
xlabel('Frame Size (Bytes)');
ylabel('Relative Frequency');

subplot(3,1,2);
hist(framesize_B,hist_bins);	
title('Distribution of B frames');
xlabel('Frame Size (Bytes)');
ylabel('Relative Frequency');

subplot(3,1,3);
hist(framesize_P,hist_bins);
title('Distribution of P frames');
xlabel('Frame Size (Bytes)');
ylabel('Relative Frequency');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   CODE FOR EXERCISE 2.3   (version: Spring 2007)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generates Plot 1. You generate Plot2 , Plot3 on
%your own. 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The next line assigns a label (figure number) to the figure 
figure(3);

initial_point=1;
ag_frame=500;
jj=initial_point;
i=1;
bytes_f=zeros(1,100);
while i<=100
while ((jj-initial_point)<=ag_frame*i && jj<length(framesize_f))
bytes_f(i)=bytes_f(i)+framesize_f(jj);
jj=jj+1;
end
i=i+1;
end

f_per_ele1 = [1:500:5e4];
subplot(3,1,1);bar(f_per_ele1, bytes_f);
xlim([1 6e4]);  
title('Video Trace @ 500 Frame Interval');
xlabel('500 Frames per element');
ylabel('Frame Size (Bytes)');

% Plot 2
initial_point=3000;
ag_frame=50;
jj=initial_point;
i=1;
bytes_f=zeros(1,100);
while i<=100
while ((jj-initial_point)<=ag_frame*i && jj<length(framesize_f))
bytes_f(i)=bytes_f(i)+framesize_f(jj);
jj=jj+1;
end
i=i+1;
end


f_per_ele2 = [3000:50:8000-50];
subplot(3,1,2);bar(f_per_ele2 ,bytes_f);
xlim([3000 9000]);
title('Video Trace @ 50 Frame Interval');
xlabel('50 Frames per element');
ylabel('Frame Size (Bytes)');


% Plot 3
initial_point=5010;
ag_frame=5;
jj=initial_point;
i=1;
bytes_f=zeros(1,100);
while i<=100
while ((jj-initial_point)<=ag_frame*i && jj<length(framesize_f))
bytes_f(i)=bytes_f(i)+framesize_f(jj);
jj=jj+1;
end
i=i+1;
end

f_per_ele3 = [5010:5:5510-5];
subplot(3,1,3);bar(f_per_ele3 , bytes_f);
xlim([5000 5600]);
title('Video Trace @ 5 Frame Interval');
xlabel('5 Frames per element');
ylabel('Frame Size (Bytes)');

