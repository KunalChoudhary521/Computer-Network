clear all; clc;

schdfile = 'ps_ex1-4.txt';

[elapsed_time, packet_size, backlog, ~, source] = textread(schdfile, '%f %f %f %f %f');

remove_idx = find(source == 1);%remove data related to source 1
elapsed_time(remove_idx) = [];
packet_size(remove_idx) = [];
backlog(remove_idx) = [];

avg = sum(packet_size) / max(cumsum(elapsed_time));
avg1 = avg*8*10e6

[elapsed_time, packet_size, backlog, ~, source] = textread(schdfile, '%f %f %f %f %f');

remove_idx = find(source == 2);%remove data related to source 2
elapsed_time(remove_idx) = [];
packet_size(remove_idx) = [];
backlog(remove_idx) = [];

avg = sum(packet_size) / max(cumsum(elapsed_time));
avg2 = avg*8*10e6