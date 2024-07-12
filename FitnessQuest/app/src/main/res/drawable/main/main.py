import numpy as np
import sys
import matplotlib.pyplot as plt
from Numerical_Quadrature_File import Numerical_Quadrature
from Jacobi_Iteration_File import Jacobi_Iteration
from Gaussian_Elimination_File import Gaussian_Elimination
##
#    Input the number of finite elements and boundary conditions
##
Ne = int(input("Enter the number of elements: "))   #  number of finite elements
y0 = np.zeros(1)
y1 = np.zeros(1)
y0[0] = input("Input the BC at x=0: ")              #  BC at x=0
y1[0] = input("Input the BC at x=1: ")              #  BC at x=1  
print("Input the quadrature rule type:")            #  quadrature rule type
print("   1. Trapezoidal rule")
print("   2. Simpson's rule")
quad_type=int(input())
##
#    Input the solver type
##   
print("Input the solver type:")
print("   1. Direct solver (Gaussian elimination)")
print("   2. Jacobi iteration")
sol_type=int(input())
if int(sol_type==2):
   epst = float(input("Input the solution tolerance: "))
   
##
#    Calculate the number of nodes and the element size
##  
N = Ne + 1                  #  number of nodes
h = 1 / Ne                  #  element size 
##
#    Initialize the global matrix and the right-hand side vector
##
A = np.zeros(N)             #  coefficient matrix main diagonal
B = np.zeros(N - 1)         #  coefficient matrix sub-diagonal
C = np.zeros(N - 1)         #  coefficient matrix super-diagonal
f = np.zeros(N)             #  right-hand side vector
##
#    Assemble the element matrices and right-hand sides
##
for e in range(Ne):                         #  loop over finite elements
    Ae = np.zeros([2,2])                    #  element coefficient matrix
    fe = np.zeros([2])                      #  element right-hand side
    for i in range(2):                      #  loop over the nodes in an element
        ## ----------------------------------------------------------------------------- ##
        ##  HERE IS THE CALL TO THE NUMERICAL QUADRATURE FUNCTION THAT YOU NEED TO WRITE ##
        fe[i]=Numerical_Quadrature(e,i,quad_type,h);  #  elelemt right-hand side vector contribution  
        ##  ---------------------------------------------------------------------------- ##
        for j in range(2):                  #  loop over nodes in an element
            Ae[i,j]=(-1)**(i+j)/h           #  element matrix contribution
##
#    Impose the boundary conditions
##            
    if e==0:                                #  the first element at x=0
       Ae[0,0]=1 
       Ae[0,1]=0 
       fe[0]=y0[0];                         #  y=y0 at x=0;
       fe[1]=fe[1]-Ae[1,0]*y0[0]            #  modify the second equation
       Ae[1,0]=0 
    if e==Ne-1:                             #  the last element at x=1
       Ae[1,1]=1 
       Ae[1,0]=0 
       fe[1]=y1[0]                          #  y=y1 at x=1 
       fe[0]=fe[0]-Ae[0,1]*y1[0]            #  modify the second equation
       Ae[0,1]=0 
##
#    Transfer the elemnt matrix and the element right-hand side to global data structures
##      
    A[e]=A[e]+Ae[0,0]                       #  diagonal element Ae[0,0]
    A[e+1]=A[e+1]+Ae[1,1]                   #  diagonal element Ae[1,1]
    B[e]=Ae[1,0]                            #  sub diagonal element Ae[1,0]
    C[e]=Ae[0,1]                            #  super diagonal element Ae[0,1] 
    f[e]=f[e]+fe[0]                         #  rhs element fe[0]
    f[e+1]=f[e+1]+fe[1]                     #  rhs element fe[1]
##
#    Solve the linear system
##
if(sol_type==1):       #  direct solver
   ## ----------------------------------------------------------------------------- ##
   ## HERE IS THE CALL TO GUSSIAN ELIMINATION FUNCTION FROM LAB 1                   ##
   x=Gaussian_Elimination(A,B,C,f)
   ## ----------------------------------------------------------------------------- ##
elif(sol_type==2):       #  Jacobi iteration
   
   x0=np.zeros(N)
   ## ----------------------------------------------------------------------------- ##
   ##  HERE IS THE CALL TO THE jacobi method FUNCTION THAT YOU NEED TO WRITE        ##
   [x,rnk,iter]=Jacobi_Iteration(A,B,C,f,x0,epst)   
   ## ----------------------------------------------------------------------------- ##
   print("Method converged in ",iter," iterations")
   print("Norm of the final residual %.6E" % rnk)
else:
   print("Invalid solver type")
##
#    Plot the solution
##
xx=np.arange(0,1.0001,h)                   #  finite element point
plt.plot(xx,x,'b',linewidth=1.5)           #  plot the solution
plt.xlabel("x")                            #  label x-axis
plt.ylabel("yh(x)")                        #  label y-axis

plt.title('Finite element solution')       #  figure title
plt.xlim((-0.05,1.05))                     #  x-axis limits
plt.ylim((min(x)-0.5,max(x)+0.5))          #  y-axis limits




# Task 3 - Testing finite element method accuracy
def test_fem_accuracy():
    element_counts = [4, 8, 16, 32, 64, 128]
    errors = []
    for Ne in element_counts:
        h = 1.0 / Ne
        x = np.linspace(0, 1, Ne + 1)
        A = 2 * np.ones(Ne + 1) + (h ** 2) * (np.pi ** 2)
        B = -1 * np.ones(Ne)
        C = -1 * np.ones(Ne)
        f = (h ** 2) * (np.pi ** 2) * np.sin(np.pi * x)
        # Apply boundary conditions
        A[0], A[-1] = 1, 1  # Strong coefficients to enforce y(0) and y(1)
        f[0], f[-1] = 0, 0  # Dirichlet boundary conditions at x=0 and x=1
        # Solve using Gaussian elimination
        y_fem = Gaussian_Elimination(A, B, C, f)
        y_exact = np.sin(np.pi * x)
        error = np.max(np.abs(y_fem - y_exact))
        errors.append(error)
        print(f'N = {Ne}: Max Error = {error}')

    plt.figure()
    plt.loglog(element_counts, errors, 'ko-')
    plt.xlabel('Number of Elements')
    plt.ylabel('Infinity Norm of Error')
    plt.title('Error Convergence')
    plt.grid(True)
    plt.show()

perform_test = input("Perform accuracy test? (y/n): ")
if perform_test.lower() == 'y':
    test_fem_accuracy()


# Task 4 Testing Jacobi's accuracy against direct method
def test_solver_accuracy():
    Ne_values = [16, 32, 64, 128, 256, 512]
    tolerances = [1e-4, 1e-6, 1e-8]
    results = {}

    for Ne in Ne_values:
        h = 1 / Ne
        x = np.linspace(0, 1, Ne + 1)
        A = 2 * np.ones(Ne + 1) - 12 * x ** 2 + 18 * x
        B = -1 * np.ones(Ne)
        C = -1 * np.ones(Ne)
        f = np.zeros(Ne + 1)
        f[0], f[-1] = 5, 1  # Dirichlet boundary conditions

        # Solve using direct method
        x_direct = Gaussian_Elimination(A, B, C, f)

        for tol in tolerances:
            x0 = np.zeros(Ne + 1)
            x_jacobi, _, num_iterations = Jacobi_Iteration(A, B, C, f, x0, tol)
            norm_diff = np.linalg.norm(x_direct - x_jacobi, np.inf)
            if Ne not in results:
                results[Ne] = {}
            results[Ne][tol] = (num_iterations, norm_diff)

    # Plotting results
    plt.figure(figsize=(10, 5))
    for tol in tolerances:
        iterations = [results[Ne][tol][0] for Ne in Ne_values]
        plt.plot(Ne_values, iterations, marker='o', label=f'Tolerance: {tol}')
    plt.xlabel('Number of Elements (N)')
    plt.ylabel('Number of Iterations')
    plt.title('Jacobi Iteration: Convergence Rate by Number of Elements')
    plt.legend()
    plt.grid(True)
    plt.show()

    plt.figure(figsize=(10, 5))
    for tol in tolerances:
        norms = [results[Ne][tol][1] for Ne in Ne_values]
        plt.plot(Ne_values, norms, marker='o', label=f'Tolerance: {tol}')
    plt.xlabel('Number of Elements (N)')
    plt.ylabel('Infinity Norm of the Difference')
    plt.title('Jacobi Iteration: Accuracy by Number of Elements')
    plt.legend()
    plt.grid(True)
    plt.show()

perform_test = input("Perform solver accuracy test? (y/n): ")
if perform_test.lower() == 'y':
    test_solver_accuracy()
