import numpy as np

def Jacobi_Iteration(A, B, C, f, x0, epsilon, max_iterations=1000):
    n = len(f)
    x_new = np.copy(x0)
    iterations = 0
    residual_norm = np.inf

    while residual_norm > epsilon and iterations < max_iterations:
        x_old = np.copy(x_new)
        # Update for tridiagonal system
        x_new[0] = (f[0] - C[0] * x_old[1]) / A[0]
        for i in range(1, n-1):
            x_new[i] = (f[i] - B[i-1] * x_old[i-1] - C[i] * x_old[i+1]) / A[i]
        x_new[n-1] = (f[n-1] - B[n-2] * x_old[n-2]) / A[n-1]

        # Recalculate the residual vector
        residual = np.zeros(n)
        residual[0] = f[0] - (A[0] * x_new[0] + C[0] * x_new[1])
        for i in range(1, n-1):
            residual[i] = f[i] - (B[i-1] * x_new[i-1] + A[i] * x_new[i] + C[i] * x_new[i+1])
        residual[n-1] = f[n-1] - (B[n-2] * x_new[n-2] + A[n-1] * x_new[n-1])

        residual_norm = np.max(np.abs(residual))
        iterations += 1

    return x_new, residual_norm, iterations
