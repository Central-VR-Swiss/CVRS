#import matplotlib.pyplot as plt
import numpy as np
from scipy.spatial import Delaunay
#from mpl_toolkits.mplot3d import Axes3D
from matplotlib.tri import Triangulation


filename = "Delaunay.dat"
f = open(filename,'r')
cnt = 0
for i in f:
    cnt+=1
f.close()
f2 = open(filename,'r')
xyz2 = np.zeros((cnt,2))

x = np.zeros(cnt)
y = np.zeros(cnt)
z = np.zeros(cnt)

def cl():
    j = 0
    for i in f2:
        xyz = i.split()
        for i in range(2):
            xyz2[j][0] = float(xyz[0])
            xyz2[j][1] = float(xyz[1])
        #xyz[2] = float(xyz[2])

        
        x[j] = xyz2[j][0]
        y[j] = xyz2[j][1]
        
        j+=1



cl()
f2.close()

print("done!")
tri = Delaunay(xyz2)
#triangle = Triangulation(x,y)
"""
fig = plt.figure()
ax = fig.add_subplot(5,1,1,projection='3d')

ax.plot_trisurf(tri,z)

plt.show()

print(tri.triangles)
"""

print(tri.simplices)
d = np.zeros((tri.simplices.shape[0],3),dtype=int)
for i in range(tri.simplices.shape[0]):
    for j in range(3):
        d[i][j] = tri.simplices[i][j]


f3 = open(filename,'w')
for i in range(d.shape[0]):
    str1 = str(d[i][0]) + " " + str(d[i][1]) + " " + str(d[i][2]) + "\n"
    f3.write(str1)
f3.close()
