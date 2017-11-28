#!/usr/bin/env python2.7
# coding: utf-8
# (File name: delaunay-test.py)
# Author: SENOO, Ken
# (Last Update: 2014-09-20T19:31+09:00)
# License: MIT

import matplotlib.pyplot as plt
import matplotlib.tri as tri
import numpy as np

def clx():
    filename = input("ファイル名を入力してください ")
    f = open(filename)
    x2 = []
    for i in f:
        xyz = i.split()
        if xyz[0]!='-9999.99':
            xyz0 = float(xyz[0])
            x2.append(xyz0)
    return x2

def cly():
    filename = input("ファイル名を入力してください ")
    f = open(filename)
    y2 = []
    for i in f:
        xyz = i.split()
        if xyz[1]!='-9999.99':
            xyz1 = float(xyz[1])
            y2.append(xyz1)
    return y2


x=clx()
y=cly()
triangle=tri.Triangulation(x,y)
plt.clf()
## plot triangle mesh
# plt.triplot(x,y,triangle.triangles, "ro-")
plt.triplot(triangle, "ro-")

## plot node label
for i in range(len(x)):
    plt.text(x[i], y[i], i)

## center of triangle
cx=[]; cy=[]
for tria in triangle.triangles:
    cx.append(np.mean([x[i] for i in tria], dtype=np.float32))
    cy.append(np.mean([y[i] for i in tria], dtype=np.float32))

## plot triangle center label
plt.plot(cx, cy, "^b")
for tria in range(len(triangle.triangles)):
    plt.text(cx[tria], cy[tria], tria)
plt.savefig("tri.png", bbox_inches="tight")