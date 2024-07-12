import numpy as np

def Gaussian_Elimination(A, B, C, f):
    n = len(f)
    # Forward elimination
    for i in range(1, n):
        mult = B[i-1] / A[i-1]
        A[i] -= mult * C[i-1]
        f[i] -= mult * f[i-1]
    
    # Backward substitution
    x = np.zeros(n)
    x[-1] = f[-1] / A[-1]
    for i in range(n-2, -1, -1):
        x[i] = (f[i] - C[i] * x[i+1]) / A[i]

    return x