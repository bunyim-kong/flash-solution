import { Routes, Route, useLocation } from "react-router-dom";
import { useLayoutEffect } from "react";
import { ShopProvider, useShop } from "./context/ShopContext";
import Header from "./components/Header";
import Footer from "./components/Footer";
import AdminLayout from "./components/AdminLayout";
import Home from "./pages/Home";
import Shop from "./pages/Shop";
import ProductDetail from "./pages/ProductDetail";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Auth from "./pages/Auth";
import Orders, { AdminOrders } from "./pages/Orders";
import SavedProducts from "./pages/SavedProducts";
import AdminCatalogue from "./pages/AdminCatalogue";
import RequestState from "./components/RequestState";
import About from "./pages/About";
import Contact from "./pages/Contact";
import AdminDashboard from "./pages/AdminDashboard";
import AdminProducts from "./pages/AdminProducts";
import AdminProductForm from "./pages/AdminProductForm";
import AdminUsers from "./pages/AdminUsers";

function ScrollToTop() {
  const { pathname } = useLocation();

  // Reset scroll after every route change, before the browser paints the new page.
  // Footer links also reset scroll when the user clicks the link for the page they are already on.
  useLayoutEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "instant" });
  }, [pathname]);

  return null;
}
function StoreLayout() {
  const { cartError, reloadCart } = useShop();
  const { pathname } = useLocation();
  return (
    <>
      <Header />
      {cartError && !["/cart", "/checkout", "/saved"].includes(pathname) && <div className="container-page"><RequestState error={cartError} reload={reloadCart} /></div>}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/shop" element={<Shop />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/login" element={<Auth />} />
        <Route path="/register" element={<Auth register />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/saved" element={<SavedProducts />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="*" element={<Home />} />
      </Routes>
      <Footer />
    </>
  );
}
function AppRoutes() {
  return (
    <>
      <ScrollToTop />
      <Routes>
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="products" element={<AdminProducts />} />
          <Route path="products/new" element={<AdminProductForm />} />
          <Route path="products/:id/edit" element={<AdminProductForm />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="catalogue" element={<AdminCatalogue />} />
          <Route path="orders" element={<AdminOrders />} />
        </Route>
        <Route path="*" element={<StoreLayout />} />
      </Routes>
    </>
  );
}
export default function App() {
  return (
    <ShopProvider>
      <AppRoutes />
    </ShopProvider>
  );
}
