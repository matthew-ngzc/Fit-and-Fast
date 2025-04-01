"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { usePathname, useRouter } from "next/navigation"

const publicRoutes = ["/auth/login", "/auth/signup"]

type AuthContextType = {
  isAuthenticated: boolean
  login: (token: string) => void
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  login: () => {},
  logout: () => {},
  isLoading: true,
})

export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()
  const pathname = usePathname()

  const isPublicRoute = publicRoutes.some((route) => pathname?.startsWith(route) || pathname === "/")

  useEffect(() => {
    const token = localStorage.getItem("token")
    setIsAuthenticated(!!token)
    setIsLoading(false)

    if (!token && !isPublicRoute) {
      router.push(`/auth/login?from=${encodeURIComponent(pathname || "/")}`)
    }
  }, [pathname, router, isPublicRoute])

  const login = (token: string) => {
    localStorage.setItem("auth-token", token)
    setIsAuthenticated(true)
  }

  const logout = () => {
    localStorage.removeItem("auth-token")
    setIsAuthenticated(false)
    router.push("/auth/login")
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout, isLoading }}>
      {isLoading ? (
        <div className="flex items-center justify-center min-h-screen">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      ) : (
        children
      )}
    </AuthContext.Provider>
  )
}