#!/usr/bin/env python2.7
# coding: utf-8
# (File name: delaunay-test.py)
# Author: SENOO, Ken
# (Last Update: 2014-09-20T19:31+09:00)
# License: MIT

import matplotlib.pyplot as plt
import matplotlib.tri as tri
import numpy as np

x = []
y = []
z = []
def cl():
    filename = input("ファイル名を入力してください ")
    f = open(filename)
    for i in f:
        xyz = i.split()
        if xyz[2]!='-9999.99':
            xyz0 = float(xyz[0])
            xyz1 = float(xyz[1])
            xyz2 = float(xyz[2])
            x.append(xyz0)
            y.append(xyz1)
            z.append(xyz2)


cl()
triangle=tri.Triangulation(x,y,z)
plt.clf()
## plot triangle mesh
# plt.triplot(x,y,triangle.triangles, "ro-")
plt.triplot(triangle, "ro-")


