# 🚀 Prochaines étapes - Développement Frontend Angular

## Phase 1 : Préparation (Aujourd'hui)

### ✅ Backend est prêt !

Le backend Java est compilé et prêt à être utilisé :
- ✅ 28 fichiers Java
- ✅ 25+ endpoints API
- ✅ Authentification JWT
- ✅ Workflow stages complet
- ✅ Statistiques et filtres

**Status** : `mvn spring-boot:run` → Application prête !

---

## Phase 2 : Frontend Angular (À faire)

### Étape 1 : Créer le projet Angular

```bash
# Créer un projet Angular 18
ng new gestion-stages-frontend
cd gestion-stages-frontend

# Installer les dépendances
npm install
npm install @angular/material @angular/cdk
npm install bootstrap
npm install ngx-toastr
npm install rxjs
```

### Étape 2 : Structure du projet

```
src/
├── app/
│   ├── core/
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── filiere.service.ts
│   │   │   ├── stage.service.ts
│   │   │   ├── file.service.ts
│   │   │   └── stats.service.ts
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   └── role.guard.ts
│   │   └── interceptors/
│   │       └── auth.interceptor.ts
│   │
│   ├── shared/
│   │   ├── components/
│   │   │   ├── navbar/
│   │   │   ├── footer/
│   │   │   └── loading/
│   │   └── models/
│   │       ├── user.model.ts
│   │       ├── filiere.model.ts
│   │       ├── stage.model.ts
│   │       └── auth.model.ts
│   │
│   ├── features/
│   │   ├── auth/
│   │   │   ├── login/
│   │   │   ├── register/
│   │   │   └── auth.module.ts
│   │   │
│   │   ├── student/
│   │   │   ├── dashboard/
│   │   │   ├── create-stage/
│   │   │   ├── my-stages/
│   │   │   ├── upload-report/
│   │   │   └── student.module.ts
│   │   │
│   │   ├── teacher/
│   │   │   ├── dashboard/
│   │   │   ├── validate-stages/
│   │   │   └── teacher.module.ts
│   │   │
│   │   ├── admin/
│   │   │   ├── dashboard/
│   │   │   ├── manage-users/
│   │   │   ├── manage-filieres/
│   │   │   ├── statistics/
│   │   │   └── admin.module.ts
│   │   │
│   │   └── filiere/
│   │       ├── list/
│   │       ├── create/
│   │       └── filiere.module.ts
│   │
│   ├── app.component.html
│   ├── app.component.ts
│   ├── app.routing.ts
│   └── app.module.ts
│
└── assets/
```

### Étape 3 : Services principaux

**auth.service.ts**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser$: Observable<any>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(
      JSON.parse(localStorage.getItem('currentUser')!)
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData)
      .pipe(tap(response => {
        if (response.token) {
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }
        return response;
      }));
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password })
      .pipe(tap(response => {
        if (response.token) {
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }
        return response;
      }));
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.currentUserValue?.token;
  }

  get userRole(): string {
    return this.currentUserValue?.role;
  }
}
```

**stage.service.ts**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StageService {
  private apiUrl = 'http://localhost:8081/api/stages';

  constructor(private http: HttpClient) { }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  create(stage: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, stage);
  }

  update(id: number, stage: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, stage);
  }

  submit(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/submit`, {});
  }

  validate(id: number, encadrantId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/validate?encadrantId=${encadrantId}`, {});
  }

  refuse(id: number, commentaire: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/refuse?commentaire=${commentaire}`, {});
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  getByEtudiant(etudiantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/etudiant/${etudiantId}`);
  }

  getByEncadrant(encadrantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/encadrant/${encadrantId}`);
  }
}
```

**stats.service.ts**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private apiUrl = 'http://localhost:8081/api/stats';

  constructor(private http: HttpClient) { }

  getSummary(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/summary`);
  }

  getByEtat(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/by-etat`);
  }

  getByFiliere(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/by-filiere`);
  }
}
```

### Étape 4 : Composants principaux

**Login Component**
```typescript
// src/app/features/auth/login/login.component.ts
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    
    this.loading = true;
    this.authService.login(
      this.loginForm.value.email,
      this.loginForm.value.password
    ).subscribe(
      response => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error => {
        this.loading = false;
        alert('Connexion échouée');
      }
    );
  }
}
```

**Stage List Component**
```typescript
// src/app/features/student/my-stages/my-stages.component.ts
export class MyStagesComponent implements OnInit {
  stages: any[] = [];
  loading = true;
  currentUser: any;

  constructor(
    private stageService: StageService,
    private authService: AuthService
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.loadStages();
  }

  loadStages(): void {
    this.stageService.getByEtudiant(this.currentUser.userId)
      .subscribe(
        stages => {
          this.stages = stages;
          this.loading = false;
        },
        error => {
          console.error('Erreur:', error);
          this.loading = false;
        }
      );
  }

  submitStage(stage: any): void {
    this.stageService.submit(stage.id).subscribe(
      () => {
        alert('Stage soumis pour validation');
        this.loadStages();
      },
      error => alert('Erreur lors de la soumission')
    );
  }
}
```

**Admin Dashboard Component**
```typescript
// src/app/features/admin/dashboard/dashboard.component.ts
export class AdminDashboardComponent implements OnInit {
  stats: any;
  loading = true;

  constructor(private statsService: StatsService) { }

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.statsService.getSummary().subscribe(
      data => {
        this.stats = data;
        this.loading = false;
      },
      error => {
        console.error('Erreur:', error);
        this.loading = false;
      }
    );
  }
}
```

### Étape 5 : Templates HTML

**Login Template**
```html
<!-- login.component.html -->
<div class="login-container">
  <div class="card">
    <h2>Connexion</h2>
    <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
      <div class="form-group">
        <label>Email</label>
        <input 
          type="email" 
          formControlName="email" 
          class="form-control"
          required
        >
        <div *ngIf="submitted && loginForm.get('email')?.errors" class="error">
          Email invalide
        </div>
      </div>

      <div class="form-group">
        <label>Mot de passe</label>
        <input 
          type="password" 
          formControlName="password" 
          class="form-control"
          required
        >
      </div>

      <button 
        type="submit" 
        class="btn btn-primary"
        [disabled]="loading"
      >
        {{ loading ? 'Connexion en cours...' : 'Connexion' }}
      </button>
    </form>
  </div>
</div>
```

**Stage List Template**
```html
<!-- my-stages.component.html -->
<div class="container">
  <h2>Mes stages</h2>

  <button class="btn btn-primary" [routerLink]="['/create-stage']">
    Créer un nouveau stage
  </button>

  <div *ngIf="loading" class="spinner">Chargement...</div>

  <table *ngIf="!loading && stages.length > 0" class="table">
    <thead>
      <tr>
        <th>Sujet</th>
        <th>Entreprise</th>
        <th>État</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let stage of stages">
        <td>{{ stage.sujet }}</td>
        <td>{{ stage.entreprise }}</td>
        <td>
          <span [ngClass]="'badge badge-' + stage.etat.toLowerCase()">
            {{ stage.etat }}
          </span>
        </td>
        <td>
          <button 
            *ngIf="stage.etat === 'BROUILLON'"
            (click)="submitStage(stage)"
            class="btn btn-sm btn-info"
          >
            Soumettre
          </button>
        </td>
      </tr>
    </tbody>
  </table>

  <div *ngIf="!loading && stages.length === 0" class="alert alert-info">
    Aucun stage créé
  </div>
</div>
```

---

## Phase 3 : Intégration & Déploiement

### Étape 6 : Configuration CORS & Proxy

**proxy.conf.json** (pour le développement)
```json
{
  "/api": {
    "target": "http://localhost:8081",
    "secure": false,
    "changeOrigin": true
  }
}
```

**angular.json**
```json
"serve": {
  "options": {
    "proxyConfig": "proxy.conf.json"
  }
}
```

### Étape 7 : Lancer en développement

```bash
# Terminal 1 : Backend
cd C:\Users\pc\IdeaProjects\authentification
mvn spring-boot:run

# Terminal 2 : Frontend
cd gestion-stages-frontend
ng serve --proxy-config proxy.conf.json
```

Frontend accessible : **http://localhost:4200**

---

## Phase 4 : Tests

### Test complet du workflow

1. ✅ Créer un compte étudiant
2. ✅ Créer un stage (BROUILLON)
3. ✅ Soumettre pour validation (EN_ATTENTE)
4. ✅ Créer un compte enseignant
5. ✅ Valider le stage (VALIDE)
6. ✅ Upload d'un rapport
7. ✅ Voir les statistiques (Admin)

---

## Phase 5 : Déploiement

### Docker

**Dockerfile** (Backend)
```dockerfile
FROM openjdk:21
COPY target/authentification-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8081
```

**Dockerfile** (Frontend)
```dockerfile
FROM node:18 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

FROM nginx:latest
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

**docker-compose.yml**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"

  backend:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      - mysql

  frontend:
    build: .
    ports:
      - "80:80"
    depends_on:
      - backend
```

### Déployer

```bash
docker-compose up -d
```

---

## ✅ Checklist Développement Frontend

- [ ] Projet Angular créé
- [ ] Services HTTP implémentés
- [ ] Authentification (login/register)
- [ ] Guard de sécurité
- [ ] Dashboard étudiant
- [ ] Dashboard enseignant
- [ ] Dashboard admin
- [ ] Gestion stages (CRUD)
- [ ] Upload rapports
- [ ] Statistiques
- [ ] Tests unitaires
- [ ] Déploiement Docker
- [ ] Tests E2E

---

## 📚 Ressources

- [Angular Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [Bootstrap](https://getbootstrap.com/)
- [RxJS](https://rxjs.dev/)

---

**Vous êtes maintenant prêt à développer le frontend ! Bonne chance ! 🚀**

